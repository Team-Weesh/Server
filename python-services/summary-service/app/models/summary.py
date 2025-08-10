from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import datetime

class ConversationMessage(BaseModel):
    """대화 메시지 모델"""
    speaker: str = Field(..., description="화자")
    message: str = Field(..., description="대화 내용")
    timestamp: Optional[datetime] = Field(None, description="메시지 시간")

class SummaryRequest(BaseModel):
    """요약 요청 모델"""
    conversation: List[ConversationMessage] = Field(
        ..., 
        description="분석할 대화 기록",
        min_items=1
    )
    max_keywords: int = Field(
        default=3, 
        description="추출할 키워드 개수",
        ge=1, 
        le=10
    )

class SummaryResponse(BaseModel):
    """요약 응답 모델"""
    keywords: List[str] = Field(..., description="추출된 핵심 키워드들")
    summary: str = Field(..., description="대화 요약")
    total_messages: int = Field(..., description="총 메시지 수")
    processing_time: float = Field(..., description="처리 시간 (초)")

class ErrorResponse(BaseModel):
    """에러 응답 모델"""
    error: str = Field(..., description="에러 메시지")
    detail: Optional[str] = Field(None, description="상세 에러 정보")
    timestamp: datetime = Field(default_factory=datetime.now)
