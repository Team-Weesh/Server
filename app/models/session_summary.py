from pydantic import BaseModel, field_validator
from typing import List, Optional, Dict, Any
from datetime import datetime

class SummaryResponse(BaseModel):
    """요약 응답 모델 (공통)"""
    keywords: List[str]
    summary: str
    total_messages: int
    processing_time: float

class SessionEndRequest(BaseModel):
    """세션 종료 요청 모델"""
    user_id: Optional[str] = None
    conversation: Optional[List[Dict[str, Any]]] = None
    max_keywords: int = 3
    
    @field_validator('user_id', 'conversation')
    @classmethod
    def validate_input(cls, v, info):
        # 현재 필드가 아닌 다른 필드들의 값을 확인
        data = info.data
        user_id = data.get('user_id')
        conversation = data.get('conversation')
        
        # 현재 검증 중인 필드가 None이고, 다른 필드도 None인 경우
        if v is None:
            if user_id is None and conversation is None:
                raise ValueError("user_id 또는 conversation 중 하나는 필수입니다.")
        return v

class SessionSummaryResponse(BaseModel):
    """세션 요약 응답 모델"""
    user_id: Optional[str]
    summary: str
    keywords: List[str]
    total_messages: int
    processing_time: float
    success: bool
    source: str  # "database", "direct", "error"
    summary_id: Optional[str] = None  # 저장된 요약 ID

class SummaryRecord(BaseModel):
    """요약 기록 모델 (DB 저장용)"""
    user_id: Optional[str]
    summary: str
    keywords: List[str]
    total_messages: int
    processing_time: float
    source: str
    created_at: datetime
    conversation_data: Optional[List[Dict[str, Any]]] = None  # 원본 대화 데이터 (선택적)
