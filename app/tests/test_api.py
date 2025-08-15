import requests

BASE_URL = "http://localhost:8000/api/v1"

def test_health():
    """헬스 체크 테스트"""
    try:
        response = requests.get(f"{BASE_URL}/summary/health")
        print(f"헬스 체크: {response.status_code} - {response.json()['status']}")
        return response.status_code == 200
    except Exception as e:
        print(f"헬스 체크: 연결 실패 - {e}")
        return False

def test_summary():
    """요약 API 테스트"""
    test_data = {
        "conversation": [
            {"speaker": "학생", "message": "학교생활이 너무 힘들어요"},
            {"speaker": "상담사", "message": "무슨일 있어요?"},
            {"speaker": "학생", "message": "친구랑 싸웠어요"},
            {"speaker": "상담사", "message": "왜 싸웠는지 알수있을까요?"}
        ],
        "max_keywords": 3
    }
    
    try:
        response = requests.post(f"{BASE_URL}/summary/analyze", json=test_data)
        if response.status_code == 200:
            result = response.json()
            print(f"요약: {result['summary']}")
            print(f"키워드: {', '.join(result['keywords'])}")
            print(f"ai 요약 API: 성공")
            return True
        else:
            print(f"요약 API: 실패 - {response.status_code}")
            return False
    except Exception as e:
        print(f"요약 API: 연결 실패 - {e}")
        return False

if __name__ == "__main__":
    print("API 테스트 시작")
    test_health()
    test_summary()
    print("테스트 완료")
