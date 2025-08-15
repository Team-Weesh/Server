import logging
import time
from typing import List, Dict, Any
from functools import wraps
from app.core.config import settings

# 로깅 설정
def setup_logging():
    """로깅 설정"""
    logging.basicConfig(
        level=getattr(logging, settings.LOG_LEVEL),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        handlers=[
            logging.StreamHandler()
        ]
    )
    return logging.getLogger(__name__)

logger = setup_logging()

def timing_decorator(func):
    """함수 실행 시간을 측정하는 데코레이터 (동기/비동기 지원)"""
    @wraps(func)
    async def async_wrapper(*args, **kwargs):
        start_time = time.time()
        result = await func(*args, **kwargs)
        end_time = time.time()
        execution_time = end_time - start_time
        logger.info(f"{func.__name__} 실행 시간: {execution_time:.2f}초")
        return result
    
    @wraps(func)
    def sync_wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        execution_time = end_time - start_time
        logger.info(f"{func.__name__} 실행 시간: {execution_time:.2f}초")
        return result
    
    # 함수가 코루틴인지 확인하여 적절한 래퍼 반환
    import inspect
    if inspect.iscoroutinefunction(func):
        return async_wrapper
    else:
        return sync_wrapper

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

