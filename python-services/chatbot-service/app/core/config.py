import os
from dotenv import load_dotenv

load_dotenv()

MongoDB_URL = os.environ.get("MONGODB_URL")
Google_api_key = os.environ.get("GEMINI_API_KEY")