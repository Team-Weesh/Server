from fastapi import APIRouter, Query
from fastapi.responses import JSONResponse
from app.models.query_input import Queryinput
from app.models.session_summary import SessionEndRequest, SessionSummaryResponse, SummaryRecord
from app.services.chatbot_service import answer_chat
from app.services.db_service import collection, save_summary, get_summaries_by_user_id
from app.services.summary_service import summary_service
from app.utils.helpers import logger
from datetime import datetime, timezone

router = APIRouter()

@router.get("/health")
async def health_check():
    """서비스 상태 확인"""
    try:
        # AI 모델 초기화 상태 확인
        current_time = datetime.now(timezone.utc).isoformat()
        
        if not summary_service.chain:
            return {
                "status": "unhealthy",
                "message": "AI 모델이 초기화되지 않았습니다.",
                "timestamp": current_time
            }
        
        return {
            "status": "healthy",
            "message": "서비스가 정상적으로 동작하고 있습니다.",
            "timestamp": current_time
        }
        
    except Exception as e:
        logger.error(f"헬스 체크 실패: {e}")
        return {
            "status": "unhealthy",
            "message": f"서비스 오류: {str(e)}",
            "timestamp": datetime.now(timezone.utc).isoformat()
        }

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
    
    try:
        if request.conversation:
            # 직접 대화 데이터 제공된 경우
            conversation_data = request.conversation
            source = "direct"
        else:
            # user_id로 DB 조회
            messages = list(collection.find({"user_id": request.user_id}).sort("time", 1))
            
            if not messages:
                return SessionSummaryResponse(
                    user_id=request.user_id,
                    summary="대화 기록이 없습니다.",
                    keywords=["대화 없음"],
                    total_messages=0,
                    processing_time=0.0,
                    success=False,
                    source="database",
                    summary_id=None
                )
            
            # DB 메시지를 요약 형식으로 변환
            conversation_data = []
            for message in messages:
                conversation_data.extend([
                    {"speaker": "user", "message": message["question"]},
                    {"speaker": "assistant", "message": message["answer"]}
                ])
            source = "database"
        
        # 공통 요약 로직
        summary_result = await summary_service.summarize_conversation(
            conversation_data, 
            request.max_keywords
        )
        
        # 요약 정보를 데이터베이스에 저장
        summary_record = SummaryRecord(
            user_id=request.user_id,
            summary=summary_result.summary,
            keywords=summary_result.keywords,
            total_messages=summary_result.total_messages,
            processing_time=summary_result.processing_time,
            source=source,
            created_at=datetime.now(timezone.utc),
            conversation_data=conversation_data if source == "direct" else None
        )
        
        summary_id = save_summary(summary_record)
        
        return SessionSummaryResponse(
            user_id=request.user_id,
            summary=summary_result.summary,
            keywords=summary_result.keywords,
            total_messages=summary_result.total_messages,
            processing_time=summary_result.processing_time,
            success=True,
            source=source,
            summary_id=summary_id
        )
        
    except Exception as e:
        logger.error(f"요약 생성 중 오류: {e}")
        return SessionSummaryResponse(
            user_id=request.user_id,
            summary="요약 생성 중 오류가 발생했습니다.",
            keywords=["오류"],
            total_messages=0,
            processing_time=0.0,
            success=False,
            source="error",
            summary_id=None
        )

@router.get("/summaries")
async def get_user_summaries(user_id: str = Query(...), limit: int = Query(default=5, le=50)):
    """사용자별 요약 목록 조회"""
    try:
        summaries = get_summaries_by_user_id(user_id, limit)
        return {
            "user_id": user_id,
            "summaries": summaries,
            "total_count": len(summaries)
        }
    except Exception as e:
        logger.error(f"사용자 요약 목록 조회 오류: {e}")
        return JSONResponse(
            status_code=500,
            content={"detail": "요약 목록 조회 중 오류가 발생했습니다."}
        )