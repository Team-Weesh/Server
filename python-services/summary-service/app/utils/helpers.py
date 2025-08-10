import logging
import time
from typing import List, Dict, Any
from functools import wraps
from app.core.config import settings
import os

# 로깅 설정
def setup_logging():
    """로깅 설정"""
    logging.basicConfig(
        level=getattr(logging, settings.LOG_LEVEL),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.StreamHandler(),
            logging.FileHandler('summary_service.log')
        ]
    )
    return logging.getLogger(__name__)

logger = setup_logging()

def timing_decorator(func):
    """함수 실행 시간을 측정하는 데코레이터"""
    @wraps(func)
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        execution_time = end_time - start_time
        logger.info(f"{func.__name__} 실행 시간: {execution_time:.2f}초")
        return result, execution_time
    return wrapper

def validate_conversation(conversation: List[Dict[str, Any]]) -> bool:
    """대화 데이터 유효성 검사"""
    if not conversation:
        return False
    
    for message in conversation:
        if not isinstance(message, dict):
            return False
        if 'speaker' not in message or 'message' not in message:
            return False
        if not message['speaker'] or not message['message']:
            return False
    
    return True

def format_conversation_text(conversation: List[Dict[str, Any]]) -> str:
    """대화 기록을 텍스트로 포맷팅"""
    formatted_text = ""
    for message in conversation:
        speaker = message.get('speaker', 'Unknown')
        content = message.get('message', '')
        formatted_text += f"{speaker}: {content}\n"
    return formatted_text.strip()

def extract_keywords_from_response(response: str) -> List[str]:
    """Gemini 응답에서 키워드 추출"""
    try:
        # 응답에서 키워드 부분만 추출
        lines = response.strip().split('\n')
        keywords = []
        
        for line in lines:
            line = line.strip()
            if line and not line.startswith('#'):
                # 번호나 불필요한 문자 제거
                keyword = line.replace('1.', '').replace('2.', '').replace('3.', '').strip()
                if keyword:
                    keywords.append(keyword)
        
        return keywords[:3]  # 최대 3개만 반환
    except Exception as e:
        logger.error(f"키워드 추출 중 오류: {e}")
        return []



def safe_get_env(key: str, default: str = "") -> str:
    """안전한 환경 변수 가져오기"""
    try:
        return os.getenv(key, default)
    except Exception as e:
        logger.error(f"환경 변수 {key} 가져오기 실패: {e}")
        return default
