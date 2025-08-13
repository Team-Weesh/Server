from fastapi import APIRouter, Query
from fastapi.responses import JSONResponse
from app.models.query_input import Queryinput
from app.models.session_summary import SessionEndRequest, SessionSummaryResponse
from app.services.chatbot_service import answer_chat
from app.services.db_service import collection
from app.services.summary_client import SummaryClient
import datetime

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

@router.post("/end-session")
async def end_session(request: SessionEndRequest):
    """대화 세션 종료 및 요약 생성"""
    
    # 요약 클라이언트 초기화
    summary_client = SummaryClient()
    
    try:
        # 1. 사용자의 대화 기록 조회
        messages = list(collection.find({"user_id": request.user_id}).sort("time", 1))
        
        if not messages:
            return SessionSummaryResponse(
                user_id=request.user_id,
                summary="대화 기록이 없습니다.",
                keywords=["대화 없음"],
                total_messages=0,
                processing_time=0.0,
                success=False
            )
        
        # 2. 대화 데이터를 요약 서비스 형식으로 변환
        conversation_data = []
        for message in messages:
            # 사용자 메시지
            conversation_data.append({
                "speaker": "user",
                "message": message["question"],
                "timestamp": message["time"].isoformat() if message.get("time") else None
            })
            # 어시스턴트 메시지
            conversation_data.append({
                "speaker": "assistant", 
                "message": message["answer"],
                "timestamp": message["time"].isoformat() if message.get("time") else None
            })
        
        # 3. 요약 서비스 호출
        summary_result = await summary_client.summarize_conversation(
            conversation_data, 
            max_keywords=request.max_keywords
        )
        
        # 4. 응답 생성
        return SessionSummaryResponse(
            user_id=request.user_id,
            summary=summary_result["summary"],
            keywords=summary_result["keywords"],
            total_messages=summary_result["total_messages"],
            processing_time=summary_result["processing_time"],
            success=True
        )
        
    except Exception as e:
        return SessionSummaryResponse(
            user_id=request.user_id,
            summary="요약 생성 중 오류가 발생했습니다.",
            keywords=["오류"],
            total_messages=0,
            processing_time=0.0,
            success=False
        )
    finally:
        await summary_client.close()