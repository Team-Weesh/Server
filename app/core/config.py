import os
from typing import Optional
from pydantic_settings import BaseSettings
from dotenv import load_dotenv

# .env 파일 로드
load_dotenv()

class Settings(BaseSettings):
    """애플리케이션 설정"""
    
    # API 설정
    API_V1_STR: str = "/api/v1"
    PROJECT_NAME: str = "AI Service"
    VERSION: str = "1.0.0"
    
    # Gemini API 설정
    GEMINI_API_KEY: Optional[str] = os.getenv("GEMINI_API_KEY")
    GEMINI_MODEL_NAME: str = "gemini-2.5-flash-lite"
    
    # MongoDB 설정
    MONGODB_URL: Optional[str] = os.getenv("MONGODB_URL")
    
    # LangSmith 설정
    LANGSMITH_API_KEY: Optional[str] = os.getenv("LANGSMITH_API_KEY")
    
    # 서버 설정
    HOST: str = "0.0.0.0"
    PORT: int = 8000
    DEBUG: bool = os.getenv("DEBUG", "False").lower() == "true"
    
    # 로깅 설정
    LOG_LEVEL: str = os.getenv("LOG_LEVEL", "INFO")
    
    class Config:
        env_file = ".env"
        case_sensitive = True

# 전역 설정 인스턴스
settings = Settings()

# LangSmith 환경변수 설정
if settings.LANGSMITH_API_KEY:
    os.environ.update({
        "LANGCHAIN_API_KEY": settings.LANGSMITH_API_KEY,
        "LANGCHAIN_PROJECT": "ai-chatbot-service",
        "LANGCHAIN_ENDPOINT": "https://api.smith.langchain.com",
        "LANGCHAIN_TRACING_V2": "true"
    })
