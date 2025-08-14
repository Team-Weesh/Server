import os
from dotenv import load_dotenv

load_dotenv()

MongoDB_URL = os.environ.get("MONGODB_URL")
Google_api_key = os.environ.get("GEMINI_API_KEY")

# LangSmith 설정
LANGSMITH_API_KEY = os.environ.get("LANGSMITH_API_KEY")

# LangSmith 환경변수 설정
if LANGSMITH_API_KEY:
    os.environ.update({
        "LANGCHAIN_API_KEY": LANGSMITH_API_KEY,
        "LANGCHAIN_PROJECT": "chatbot-service",
        "LANGCHAIN_ENDPOINT": "https://api.smith.langchain.com",
        "LANGCHAIN_TRACING_V2": "true"
    })