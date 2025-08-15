import asyncio
import os
from pathlib import Path
project_root = Path(__file__).parent.parent.parent  # Server 디렉토리
sys.path.insert(0, str(project_root))

from app.services.summary_service import SummaryService

async def test_summary():
    """간단한 요약 기능 테스트"""
    print("SummaryService 테스트 시작")
    
    # API 키 확인
    if not os.getenv("GEMINI_API_KEY"):
        print("GEMINI_API_KEY가 설정되지 않았습니다.")
        return
    
    # 테스트 대화 데이터
    test_conversation = [
        {"speaker": "학생", "message": "학교생활이 너무 힘들어요ㅜ"},
        {"speaker": "상담사", "message": "무슨일 있어요? 편하게 말해도 돼요"},
        {"speaker": "학생", "message": "저번주에 친구랑 싸웠어요. 근데 어떻게 해야할지 모르겠어요"},
        {"speaker": "상담사", "message": "친구랑 왜 싸웠는지 알수있을까요?"}
    ]
    
    try:
        # 서비스 초기화
        service = SummaryService()
        
        # 요약 실행
        result = await service.summarize_conversation(
            conversation=test_conversation,
            max_keywords=3
        )
        
        # 결과 출력
        print(f"요약: {result.summary}")
        print(f"키워드: {result.keywords}")
        print(f"메시지 수: {result.total_messages}")
        print(f"처리 시간: {result.processing_time:.2f}초")
        
        print("테스트 성공!")
        
    except Exception as e:
        print(f"테스트 실패: {e}")

if __name__ == "__main__":
    asyncio.run(test_summary())
