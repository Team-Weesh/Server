from pydantic import BaseModel
from typing import List

class SessionEndRequest(BaseModel):
    """세션 종료 요청 모델"""
    user_id: str
    max_keywords: int = 3

class SessionSummaryResponse(BaseModel):
    """세션 요약 응답 모델"""
    user_id: str
    summary: str
    keywords: List[str]
    total_messages: int
    processing_time: float
    success: bool
