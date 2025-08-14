from fastapi import FastAPI
from app.api.routes import chatbot
import uvicorn

app = FastAPI()
app.include_router(chatbot.router)

@app.get("/")
async def root():
  return {"Message":"Go to localhost:8000/docs",
          "AAAA" : "/docs 로 가세요"}

if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)