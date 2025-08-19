from pymongo import MongoClient
from app.core.config import settings
from app.models.session_summary import SummaryRecord
from datetime import datetime
import uuid

client = MongoClient(settings.MONGODB_URL)
db=client["chat"]
collection = db["chat_log"]
summary_collection = db["summaries"]  # 요약 저장용 컬렉션

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

def save_summary(summary_record: SummaryRecord) -> str:
    """요약 정보를 데이터베이스에 저장"""
    try:
        # 고유 ID 생성
        summary_id = str(uuid.uuid4())
        
        # 저장할 문서 생성
        summary_doc = {
            "_id": summary_id,
            "user_id": summary_record.user_id,
            "summary": summary_record.summary,
            "keywords": summary_record.keywords,
            "total_messages": summary_record.total_messages,
            "processing_time": summary_record.processing_time,
            "source": summary_record.source,
            "created_at": summary_record.created_at,
            "conversation_data": summary_record.conversation_data
        }
        
        # 데이터베이스에 저장
        summary_collection.insert_one(summary_doc)
        
        print(f"✅ 요약 저장 완료: {summary_id}")
        return summary_id
        
    except Exception as e:
        print(f"⚠️ 요약 저장 오류: {e}")
        raise e

def get_summaries_by_user_id(user_id: str, limit: int = 10):
    """사용자별 요약 목록 조회"""
    try:
        summaries = list(summary_collection.find(
            {"user_id": user_id}
        ).sort("created_at", -1).limit(limit))
        return summaries
    except Exception as e:
        print(f"⚠️ 사용자 요약 목록 조회 오류: {e}")
        return []