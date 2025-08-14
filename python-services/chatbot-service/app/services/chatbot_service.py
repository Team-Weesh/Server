import datetime
import os
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.output_parsers import StrOutputParser
from langchain_core.messages import HumanMessage, AIMessage
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
  # 대화 기록을 메시지 형태로 변환
  chat_history = []
  try:
      if history := get_conversation_history(user_id):
          # JSON 형태의 대화 기록을 HumanMessage와 AIMessage로 변환
          for msg in history:
              if msg.get("role") == "user" and msg.get("content"):
                  chat_history.append(HumanMessage(content=msg["content"]))
              elif msg.get("role") == "assistant" and msg.get("content"):
                  chat_history.append(AIMessage(content=msg["content"]))
  except Exception as e:
      print(f"⚠️ 대화 기록 처리 오류: {e}")
      chat_history = []  # 오류 발생 시 빈 기록으로 시작
  
  # LangSmith 추적을 위한 메타데이터
  metadata = {
      "user_id": user_id,
      "timestamp": datetime.datetime.now().isoformat(),
      "has_history": bool(chat_history),
      "history_length": len(chat_history)
  }
  
  try:
      # Instruction + Context Separation 방식으로 체인 실행
      result = tax_chain.invoke(
          {
              "chat_history": chat_history,
              "question": question
          },
          config={"metadata": metadata}
      )
      
      # LangSmith 추적 완료
  except Exception as e:
        print(f"❌ tax_chain 실행 오류: {e}")
        # 기본 응답
        result = "죄송해요, 일시적인 오류가 발생했어요."
  
  try:
      save_chat(user_id, question, result, datetime.datetime.now())
  except Exception as e:
      print(f"⚠️ 대화 저장 오류: {e}")
  
  return result