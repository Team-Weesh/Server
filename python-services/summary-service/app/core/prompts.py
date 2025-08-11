PROMPT_TEMPLATE = """
다음 대화 내용을 분석해주세요:

{conversation_text}

다음 두 가지를 제공해주세요:

1. 대화 요약 (한 문단으로 간결하게):
2. 핵심 키워드 {max_keywords}개 (한 줄에 하나씩):

응답 형식:
요약: [대화 요약 내용]

키워드:
1. [키워드1]
2. [키워드2]
3. [키워드3]
"""

def get_prompt_template() -> str:
    """프롬프트 템플릿 가져오기"""
    return PROMPT_TEMPLATE
