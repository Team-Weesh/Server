from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import datetime

class SummaryRequest(BaseModel):
    """요약 요청 모델"""
    conversation: List[Dict[str, Any]] = Field(
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


