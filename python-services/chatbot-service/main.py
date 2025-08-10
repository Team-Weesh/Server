from fastapi import FastAPI
from app.api.routes import chatbot
app = FastAPI()
app.include_router(chatbot.router)

@app.get("/")
async def root():
  return {"Message":"Go to loacalhost:8000/docs",
          "AAAA" : "/docs 로 가세여"}