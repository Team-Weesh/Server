# AI feature branch

ai 요약 테스트

```
curl -X POST "http://localhost:8000/api/v1/end-session" \
  -H "Content-Type: application/json" \
  -d '{
    "conversation": [
      {"speaker": "학생", "message": "학교생활이 너무 힘들어요"},
      {"speaker": "상담사", "message": "무슨일 있어요?"}
    ],
    "max_keywords": 3
  }'
```




```
curl -X POST "http://localhost:8000/api/v1/end-session" \
  -H "Content-Type: application/json" \
  -d '{
    "user_id": "user123",
    "max_keywords": 3
  }'
```