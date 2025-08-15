from pydantic import BaseModel

class Queryinput(BaseModel):
    id: str
    question: str