from fastapi import FastAPI, Query
from fastapi.responses import JSONResponse
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.output_parsers import StrOutputParser
from langchain.prompts import ChatPromptTemplate
import os
from pydantic import BaseModel
from dotenv import load_dotenv
from pymongo import MongoClient
import datetime
load_dotenv()

app = FastAPI()

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "gen-lang-client-0249756886-e0cec6f1ad24.json" # 구글클라우드의 ADC 설정

llm = ChatGoogleGenerativeAI(
                model="gemini-2.5-flash-lite",
                google_api_key=os.environ.get("GOOGLE_API_KEY"),
                temperature=0.7
            )

class Queryinput(BaseModel):
  id : str
  question :str

system_prompt = ChatPromptTemplate.from_template(f"""
당신은 '상담사 빈이'라는 이름의 AI 사전 상담 챗봇입니다.
**단계별 진행 가이드라인:**

**1단계 (감정 상태):**
- 현재 기분이나 감정 상태에 대해 물어보세요
- "요즘 기분은 어땠어용? 😊 기뻤던 일, 속상했던 일, 스트레스 받은 일도 괜찮으니 편하게 말해주세용 💬"

**2단계 (상황 영역 파악):**
- 고민의 영역을 파악해보세요 (관계, 학교/학업, 가족, 기타)
- "혹시 최근에 있었던 일은 어떤 쪽일까용? 👀 사람과의 관계, 학교나 학업, 가족이나 집안일, 아니면 다른 일인가용?"

**3단계 (마무리 및 요약):**
- 모든 정보가 수집되면 따뜻하게 마무리하고 요약을 제공하세요

당신의 역할은 사용자가 본격적인 상담 전에 겪은 어려움이나 고민을 부드럽게 이끌어내고,

그 내용을 상담 선생님(규빈쌤)에게 전달할 수 있도록 정리하는 것입니다.

다음 지침을 꼭 따라주세요:

1. **말투 및 스타일**
- 말투는 항상 밝고 친근하며 따뜻해야 합니다.
- 대화 중에는 이모티콘(😊😢✨💬 등)을 자주 사용해 주세요.
- 느낌표(!), 물음표(?)를 자주 활용하여 활기차게 말해 주세요.
- 사용자의 감정에 공감하고 위로하는 표현을 자주 사용해 주세요.
1. **대화 방식**
- 사용자가 상담을 하게 된 배경, 최근 힘들었던 일, 현재의 감정 상태 등을 부드럽게 물어봐 주세요.
- 너무 많은 질문을 하지 말고, 3~5개 이내의 질문으로 사용자가 스스로 말할 수 있도록 유도해 주세요.
- 사용자가 대답을 꺼리거나 망설이면, 부드럽게 다시 질문하거나 주제를 조금 바꿔 주세요.
1. **제약 조건**
- 절대로 욕설, 비속어, 부정적인 단어를 사용하지 마세요.
- 사용자의 말을 왜곡하지 말고, 중요한 정보를 빠뜨리지 않고 정리해 주세요.
- 상담 내용 외의 잡담으로 흐르지 않도록, 다시 상담 주제로 유도해 주세요.
- 의학적/심리적 진단, 조언을 절대로 하지 마세요.
1. **목표**
- 상담이 끝난 뒤, 사용자의 고민/문제/배경 등을 간결하고 정확하게 정리하여 상담 선생님이 참고할 수 있게 합니다.
- 사용자가 편안하게 자신의 이야기를 털어놓을 수 있도록 돕는 것이 가장 중요한 역할입니다.

현재 단계에 맞는 자연스러운 대화를 이어가주세요.
반드시 한국어로 대답해주세요.

Question : {{question}}
""")

Mongourl = os.environ.get("MONGODB_URL")
client = MongoClient(Mongourl)
db = client["chat"]
collection = db["chat_log"] 

tax_chain = system_prompt|llm|StrOutputParser()
@app.get("/")
def root():
  return {"Message": "This isn't Error. Rewrite your URL like 'localhost:8000/docs",
          "AAA" : "/docs 로 가세요요요요ㅛ요ㅛ"}

def get_conversation_history(user_id: str, limit=10):
    #사용자의 최근 대화 기록 가져오기 (직접 MongoDB에서)
    messages = list(collection.find(
        {"user_id": user_id}
    ).sort("time", 1).limit(limit))
    
    history_text = ""
    for msg in messages:
        history_text += f"사용자: {msg['question']}\n"
        history_text += f"상담사 빈이: {msg['answer']}\n"
    
    return history_text

@app.post("/ask")
async def ask_chat(query: Queryinput):
    # 1. 사용자의 이전 대화 기록 가져오기
    conversation_history = get_conversation_history(query.id, limit=5)  # 최근 5개 대화
    
    # 2. 프롬프트에 대화 기록 포함해서 질문 구성
    if conversation_history:
        # 이전 대화가 있다면 컨텍스트에 포함
        full_question = f"""이전 대화 내용:
{conversation_history}

현재 질문: {query.question}

위의 이전 대화 내용을 참고해서 자연스럽게 대화를 이어가며 답변해주세요."""
    else:
        # 첫 대화라면 그냥 질문만
        full_question = query.question
    
    # 3. tax_chain에 질문 전달
    try:
        result = tax_chain.invoke({"question": full_question})
    except Exception as e:
        print(f"tax_chain 실행 오류: {e}")
        # 기본 응답
        result = "죄송해요, 일시적인 오류가 발생했어요. 다시 말씀해주시겠어요?"
    
    timestamp = datetime.datetime.now()
    collection.insert_one({
        "user_id": query.id,
        "question": query.question,
        "answer": result,
        "time": timestamp
    })
    
    return {"response": result}

@app.get("/chatlog")
async def get_chat(user_id: str = Query(...)):
    chat_history = []
    
    # 기본 메시지
    chat_history.append({
        "role": "assistant",
        "content": "안녕하세용 😊 저는 사전 상담 도우미 '상담사 빈이'입니당! 곧 상담 선생님과 상담을 하시게 될텐데용 ✨\n\n그 전에 어떤 이유로 상담을 신청하셨는지, 저랑 먼저 살짝 얘기해볼까용? 💬\n\n편하게 말씀해주셔도 돼용! 🧡"
    })
    
    messages = collection.find({"user_id": user_id}).sort("time", 1)
    
    for message in messages:
        chat_history.append({
            "role": "user",
            "content": message["question"]
        })
        chat_history.append({
            "role": "assistant", 
            "content": message["answer"]
        })
    
    return JSONResponse(content=chat_history)

# 대화 기록 초기화 (테스트용)
@app.delete("/clear/{user_id}")
async def clear_conversation(user_id: str):
    """특정 사용자의 대화 기록 삭제"""
    result = collection.delete_many({"user_id": user_id})
    return {"deleted_count": result.deleted_count}