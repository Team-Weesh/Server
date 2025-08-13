import httpx
from typing import List, Dict, Any
import logging

logger = logging.getLogger(__name__)

class SummaryClient:
    def __init__(self, base_url: str = "http://localhost:8001"):
        self.base_url = base_url
        self.client = httpx.AsyncClient()
    
    async def summarize_conversation(self, conversation: List[Dict[str, Any]], max_keywords: int = 3):
        """요약 서비스에 대화 요약 요청"""
        url = f"{self.base_url}/api/v1/summary/analyze"
        payload = {
            "conversation": conversation,
            "max_keywords": max_keywords
        }
        
        try:
            logger.info(f"요약 서비스 호출: {len(conversation)}개 메시지")
            response = await self.client.post(url, json=payload)
            response.raise_for_status()
            result = response.json()
            logger.info(f"요약 완료: {len(result.get('keywords', []))}개 키워드")
            return result
        except httpx.HTTPStatusError as e:
            logger.error(f"요약 서비스 HTTP 오류: {e.response.status_code}")
            return self._create_error_response(f"요약 서비스 오류: {e.response.status_code}", len(conversation))
        except Exception as e:
            logger.error(f"요약 서비스 호출 실패: {e}")
            return self._create_error_response(f"요약 서비스 연결 실패", len(conversation))
    
    def _create_error_response(self, error_message: str, total_messages: int):
        """에러 응답 생성"""
        return {
            "keywords": ["요약 실패"],
            "summary": error_message,
            "total_messages": total_messages,
            "processing_time": 0.0
        }
    
    async def close(self):
        """클라이언트 종료"""
        await self.client.aclose()
