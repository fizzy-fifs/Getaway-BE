import asyncio
import os

from dotenv import load_dotenv
from langchain.agents import initialize_agent
from langchain.memory import ConversationBufferWindowMemory
from langchain_community.chat_models import ChatOpenAI

from WebPageTool import AccommodationWebPageTool
from extractor import extract
from schemas import airbnb_schema
from scrape import scrape

load_dotenv()

open_ai_api_key = os.getenv('OPENAI_API_KEY')
llm = ChatOpenAI(temperature=0, model="gpt-4-0125-preview", api_key=open_ai_api_key)

# TESTING
if __name__ == "__main__":
    token_limit = 4000


    async def scrape_with_llm():
        accomm_tool = AccommodationWebPageTool()
        tools = [accomm_tool]
        memory = ConversationBufferWindowMemory(
            memory_key='chat_history',
            k=10,
            return_messages=True
        )
        fixed_prompt = '''Assistant is a large language model trained by OpenAI.

        Assistant is designed to be able to assist with a wide range of tasks, from answering simple questions to 
        providing in-depth explanations and discussions on a wide range of topics. As a language model, Assistant is 
        able to generate human-like text based on the input it receives, allowing it to engage in natural-sounding 
        conversations and provide responses that are coherent and relevant to the topic at hand.
        
        Assistant does not know information about content on webpages or accommodations and should always use the 
        accomm_tool if asked.
        
        Overall, Assistant is a powerful system that can help with a wide range of tasks and provide valuable 
        insights and information on a wide range of topics. Whether you need help with a specific question or just 
        want to have a conversation about a particular topic, Assistant is here to assist.'''

        conversational_agent = initialize_agent(
            agent='chat-conversational-react-description',
            tools=tools,
            llm=llm,
            verbose=True,
            max_iterations=10,
            memory=memory
        )

        conversational_agent.agent.llm_chain.prompt.messages[0].prompt.template = fixed_prompt
        url = "https://www.airbnb.co.uk/?checkin=2024-12-18&checkout=2024-12-27&adults=10&price_filter_num_nights=9&price_max=610&price_min=590"

        conversational_agent.run(f"Extract all the accommodation from: {url}. Your output should be a list containing all the accommodations in the given url. For each accommodation you are to extract the following properties: 1. Location 2. Total price 3. The url that leads to the accommodation's page 4. All image urls")

        # html_content = await scrape(url)
        # html_content_fits_content_window = html_content[:token_limit]
        #
        # extracted_content = await extract(html_content_fits_content_window, schema=airbnb_schema)
        # print(extracted_content)
        # print(extracted_content.count("listing_location"))




    asyncio.run(scrape_with_llm())
