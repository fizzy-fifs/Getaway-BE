import os

from dotenv import load_dotenv
from langchain.chains import create_extraction_chain, create_extraction_chain_pydantic
from langchain_openai import ChatOpenAI

load_dotenv()

open_ai_api_key = os.getenv('OPENAI_API_KEY')
llm = ChatOpenAI(temperature=0, model="gpt-3.5-turbo-0125", open_ai_api_key=open_ai_api_key)


async def extract(content: str, **kwargs):
    """
    Async function to extract scraped content using an LLM model and a given schema
    """

    print(f"Extracting content using {llm.get_name()}...")

    if 'schema_pydantic' in kwargs:
        response = create_extraction_chain_pydantic(pydantic_schema=kwargs['schema_pydantic'], llm=llm).run(content)

        response_as_dict = [item.dict() for item in response]

        return response_as_dict

    else:
        return create_extraction_chain(schema=kwargs['schema'], llm=llm).run(content)
