from pymongo import MongoClient
from app.core.config import settings

client = MongoClient(settings.MONGODB_URL)
db=client["chat"]
collection = db["chat_log"]

def get_conversation_history(user_id: str, limit=10):
    try:
        #사용자의 최근 대화 기록 가져오기 (직접 MongoDB에서)
        messages = list(collection.find(
            {"user_id": user_id}
        ).sort("time", 1).limit(limit))
        
        # JSON 형태로 대화 기록 구성 (줄바꿈 문제 해결)
        history_data = []
        for msg in messages:
            if msg.get('question') and msg.get('answer'):
                history_data.append({
                    "role": "user",
                    "content": msg['question']
                })
                history_data.append({
                    "role": "assistant", 
                    "content": msg['answer']
                })
        
        return history_data
    except Exception as e:
        print(f"⚠️ 대화 기록 조회 오류: {e}")
        return []

def save_chat(user_id, question, answer, timestamp):
    try:
        collection.insert_one({
            "user_id": user_id,
            "question": question,
            "answer": answer,
            "time": timestamp
        })
    except Exception as e:
        print(f"⚠️ 대화 저장 오류: {e}")
        raise e