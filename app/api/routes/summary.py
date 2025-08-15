from fastapi import APIRouter, HTTPException, status
from app.models.summary import SummaryRequest, SummaryResponse
from app.services.summary_service import summary_service
from app.utils.helpers import logger
from datetime import datetime, timezone

router = APIRouter(prefix="/summary", tags=["summary"])

@router.post(
    "/analyze",
    response_model=SummaryResponse
)
async def analyze_conversation(request: SummaryRequest) -> SummaryResponse:
    """대화 요약 및 키워드 추출 API"""

    try:
        logger.info(f"요약 요청 받음: {len(request.conversation)}개 메시지, {request.max_keywords}개 키워드")
        
        # 대화 데이터를 딕셔너리 리스트로 변환
        conversation_data = []
        for message in request.conversation:
            conversation_data.append({
                "speaker": message.speaker,
                "message": message.message,
                "timestamp": message.timestamp.isoformat() if message.timestamp else None
            })
        
        # 요약 서비스 호출
        result = await summary_service.summarize_conversation(
            conversation=conversation_data,
            max_keywords=request.max_keywords
        )
        
        # 에러 응답인지 확인
        error_prefixes = {
            "잘못된 대화 데이터": (status.HTTP_400_BAD_REQUEST, "잘못된 대화 데이터입니다. speaker와 message 필드가 필요합니다."),
            "AI 모델 초기화 실패": (status.HTTP_503_SERVICE_UNAVAILABLE, "AI 모델 서비스를 사용할 수 없습니다. API 키를 확인해주세요."),
            "요약 처리 실패": (status.HTTP_500_INTERNAL_SERVER_ERROR, "요약 처리 중 오류가 발생했습니다.")
        }
        
        for prefix, (status_code, detail) in error_prefixes.items():
            if result.summary.startswith(prefix):
                raise HTTPException(status_code=status_code, detail=detail)
        
        return result
        
    except HTTPException:
        # 이미 HTTPException이 발생한 경우 그대로 재발생
        raise
    except Exception as e:
        logger.error(f"예상치 못한 오류: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="서버 내부 오류가 발생했습니다."
        )

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
