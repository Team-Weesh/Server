import asyncio
import re
import time
from typing import List, Dict, Any, Tuple
from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser

from app.core.config import settings
from app.core.prompt import get_prompt_template
from app.models.summary import SummaryResponse
from app.utils.helpers import (
    logger, 
    timing_decorator, 
    validate_conversation, 
    format_conversation_text
)

class SummaryService:
    """대화 요약 서비스"""
    
    def __init__(self):
        """서비스 초기화"""
        self.llm = None
        self.chain = None
        self._initialize_model()
    
    def _initialize_model(self):
        """Gemini 모델 초기화"""
        try:
            if not settings.GEMINI_API_KEY:
                logger.error("GEMINI_API_KEY가 설정되지 않았습니다.")
                return
            
            # Gemini 모델 초기화
            self.llm = ChatGoogleGenerativeAI(
                model=settings.GEMINI_MODEL_NAME,
                google_api_key=settings.GEMINI_API_KEY,
                temperature=0.3,
                max_output_tokens=1000
            )
            
            # 프롬프트 템플릿 생성
            prompt_text = get_prompt_template()
            prompt_template = ChatPromptTemplate.from_template(prompt_text)
            
            # 체인 구성
            self.chain = prompt_template | self.llm | StrOutputParser()
            
            logger.info(f"Gemini 모델 초기화 완료: {settings.GEMINI_MODEL_NAME}")
            return
            
        except Exception as e:
            logger.error(f"Gemini 모델 초기화 실패: {e}")
            self.llm = None
            self.chain = None
    
    async def summarize_conversation(
        self, 
        conversation: List[Dict[str, Any]], 
        max_keywords: int = 3
    ) -> SummaryResponse:
        """대화 요약 및 키워드 추출"""
        
        # 데이터 검증
        if not validate_conversation(conversation):
            logger.error("잘못된 대화 데이터")
            return self._create_error_response("잘못된 대화 데이터", 0, 0.0)
        
        # 모델 초기화 확인
        if not self.chain:
            logger.error("AI 모델이 초기화되지 않았습니다.")
            return self._create_error_response("AI 모델 초기화 실패", len(conversation), 0.0)
        
        try:
            # 실행 시간 측정 시작
            start_time = time.time()
            
            # 대화 텍스트 변환
            conversation_text = format_conversation_text(conversation)
            
            # AI 모델 호출
            response = await self._call_gemini_model(conversation_text, max_keywords)
            
            # 응답 파싱
            summary, keywords = self._parse_gemini_response(response)
            
            # 실행 시간 측정 완료
            end_time = time.time()
            processing_time = end_time - start_time
            
            return SummaryResponse(
                keywords=keywords,
                summary=summary,
                total_messages=len(conversation),
                processing_time=processing_time
            )
            
        except Exception as e:
            logger.error(f"요약 처리 중 오류: {e}")
            return self._create_error_response(f"요약 처리 실패: {str(e)}", len(conversation), 0.0)
    
    async def _call_gemini_model(self, conversation_text: str, max_keywords: int) -> str:
        """Gemini 모델 호출"""
        try:
            # LangChain 체인 직접 호출
            response = await self.chain.ainvoke({
                "conversation_text": conversation_text,
                "max_keywords": max_keywords
            })
            return response
            
        except Exception as e:
            logger.error(f"Gemini API 호출 실패: {e}")
            raise e
    
    def _parse_gemini_response(self, response: str) -> Tuple[str, List[str]]:
        """Gemini 응답 파싱"""
        try:
            lines = response.strip().split('\n')
            summary = ""
            keywords = []
            
            in_summary = False
            in_keywords = False
            
            for line in lines:
                line = line.strip()
                
                if line.startswith("요약:"):
                    in_summary = True
                    in_keywords = False
                    summary = line.replace("요약:", "").strip()
                    continue
                
                elif line.startswith("키워드:"):
                    in_summary = False
                    in_keywords = True
                    continue
                
                elif in_summary and line:
                    summary += " " + line
                
                elif in_keywords and line:
                    # 번호 제거 (1., 2., 3. 등)
                    keyword = re.sub(r'^\d+\.\s*', '', line).strip()
                    if keyword:
                        keywords.append(keyword)
            
            # 기본값 처리
            if not summary:
                summary = "대화 내용을 요약할 수 없습니다."
            
            if not keywords:
                keywords = ["키워드 추출 실패"]
            
            return summary, keywords
            
        except Exception as e:
            logger.error(f"응답 파싱 실패: {e}")
            return "응답 파싱에 실패했습니다.", ["파싱 오류"]
    
    def _create_error_response(self, error_message: str, total_messages: int, processing_time: float) -> SummaryResponse:
        """에러 응답 생성"""
        return SummaryResponse(
            keywords=["오류 발생"],
            summary=error_message,
            total_messages=total_messages,
            processing_time=processing_time
        )

# 전역 서비스 인스턴스
summary_service = SummaryService()
