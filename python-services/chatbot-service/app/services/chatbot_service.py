import datetime
import os
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from langchain_core.tracers import LangChainTracer
from app.core.config import Google_api_key, LANGSMITH_API_KEY
from app.core.prompt import system_prompt
from app.services.db_service import get_conversation_history, save_chat

# API 키 검증
if not Google_api_key:
    raise ValueError("Google API 키가 설정되지 않았습니다. GEMINI_API_KEY 환경변수를 확인해주세요.")

# LangSmith 설정
if LANGSMITH_API_KEY:
    print("🔍 LangSmith 추적 활성화")
else:
    print("⚠️ LangSmith 추적 비활성화")

llm = ChatGoogleGenerativeAI(
  model="gemini-2.5-flash-lite",
  google_api_key=Google_api_key,
  temperature=0.7
)

# LangSmith 추적을 위한 체인 구성
tax_chain = system_prompt|llm|StrOutputParser()

def answer_chat(user_id, question):
  history = get_conversation_history(user_id)
  full_question =  f"이전 대화 내용:\n{history}\n\n현재 질문: {question}" if history else question
  
  # LangSmith 추적을 위한 메타데이터
  metadata = {
      "user_id": user_id,
      "timestamp": datetime.datetime.now().isoformat(),
      "has_history": bool(history),
      "history_length": len(history.split('\n')) if history else 0
  }
  
  try:
      # LangSmith 추적과 함께 체인 실행
      result = tax_chain.invoke(
          {"question": full_question},
          config={"metadata": metadata}
      )
      
      # LangSmith 추적 완료 (웹사이트에서 확인 가능)
  except Exception as e:
        print(f"❌ tax_chain 실행 오류: {e}")
        # 기본 응답
        result = "죄송해요, 일시적인 오류가 발생했어요."
  
  save_chat(user_id, question, result, datetime.datetime.now())
  return result