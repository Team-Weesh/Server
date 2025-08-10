import os
from dotenv import load_dotenv

load_dotenv()

MongoDB_URL = os.environ.get("MONGODB_URL")
Google_api_key = os.environ.get("GOOGLE_API_KEY")
Google_credentials = "python-services\chatbot-service\gen-lang-client-0249756886-e0cec6f1ad24.json"

os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = Google_credentials