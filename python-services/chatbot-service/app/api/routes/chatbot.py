from fastapi import APIRouter, Query
from fastapi.responses import JSONResponse
from app.models.query_input import Queryinput
from app.services.chatbot_service import answer_chat
from app.services.db_service import collection

router = APIRouter()

@router.post("/ask")
async def ask_chat(query: Queryinput):
  return {"response" : answer_chat(query.id, query.question)}

@router.get("/chatlog")
async def get_chat(user_id: str = Query(...)):
  chat_history=[{
        "role": "assistant",
        "content": "안녕하세용 😊 저는 사전 상담 도우미 '상담사 빈이'입니당! 곧 상담 선생님과 상담을 하시게 될텐데용 ✨\n\n그 전에 어떤 이유로 상담을 신청하셨는지, 저랑 먼저 살짝 얘기해볼까용? 💬\n\n편하게 말씀해주셔도 돼용! 🧡"
  }]
  messages = collection.find({"user_id": user_id}).sort("time", 1)
  for message in messages:
      chat_history.extend([
          {"role": "user", "content": message["question"]},
          {"role": "assistant", "content": message["answer"]}
      ])
  return JSONResponse(content=chat_history)