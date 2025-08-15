from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from contextlib import asynccontextmanager
import time

from app.core.config import settings
from app.api.routes.summary import router as summary_router
from app.api.routes.chatbot import router as chatbot_router
from app.utils.helpers import logger

# Lifespan 이벤트 핸들러
@asynccontextmanager
async def lifespan(app: FastAPI):
    # 시작 이벤트
    logger.info(f"{settings.PROJECT_NAME} v{settings.VERSION} 시작")
    logger.info(f"서버 주소: http://{settings.HOST}:{settings.PORT}")
    
    yield
    
    # 종료 이벤트
    logger.info(f"{settings.PROJECT_NAME} 종료")

# FastAPI 애플리케이션 생성
app = FastAPI(
    title=settings.PROJECT_NAME,
    version=settings.VERSION,
    description="AI 상담 챗봇 및 대화 요약 서비스",
    docs_url="/docs",
    redoc_url="/redoc",
    lifespan=lifespan
)

# CORS 미들웨어 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 개발 환경용, 프로덕션에서는 특정 도메인으로 제한
    allow_credentials=True,
    allow_methods=["GET", "POST"],
    allow_headers=["*"],
)

# 요청 처리 시간 측정 미들웨어
@app.middleware("http")
async def add_process_time_header(request: Request, call_next):
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    response.headers["X-Process-Time"] = str(process_time)
    return response

# 라우터 등록
app.include_router(summary_router, prefix=settings.API_V1_STR)
app.include_router(chatbot_router, prefix=settings.API_V1_STR)

# 루트 엔드포인트
@app.get("/")
async def root():
    """루트 엔드포인트"""
    return {
        "message": f"{settings.PROJECT_NAME} v{settings.VERSION}",
        "health": f"{settings.API_V1_STR}/summary/health",
        "docs": "/docs",
        "AAAA": "/docs 로 가세요"
    }

# 전역 예외 처리
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """전역 예외 처리"""
    if not hasattr(exc, 'status_code'):
        logger.error(f"전역 예외 발생: {exc}")
    
    return JSONResponse(
        status_code=500,
        content={
            "error": "서버 내부 오류가 발생했습니다.",
            "detail": str(exc) if getattr(settings, 'DEBUG', False) else "Internal server error"
        }
    )


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "main:app",
        host=settings.HOST,
        port=settings.PORT,
        reload=settings.DEBUG,
        log_level=settings.LOG_LEVEL.lower()
    )
