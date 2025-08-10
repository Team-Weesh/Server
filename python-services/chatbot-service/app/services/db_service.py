from pymongo import MongoClient
from app.core.config import MongoDB_URL

client = MongoClient(MongoDB_URL)
db=client["chat"]
collection = db["chat_log"]

def get_conversation_history(user_id: str, limit=10):
    #사용자의 최근 대화 기록 가져오기 (직접 MongoDB에서)
    messages = list(collection.find(
        {"user_id": user_id}
    ).sort("time", 1).limit(limit))
    
    history_text = ""
    for msg in messages:
        history_text += f"user: {msg['question']}\n"
        history_text += f"assistant: {msg['answer']}\n"
    
    return history_text

def save_chat(user_id,question,answer,timestamp):
    collection.insert_one({
        "user_id": user_id,
        "question": question,
        "answer": answer,
        "time": timestamp
    })