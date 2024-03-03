import asyncio

from extractor import extract
from schemas import airbnb_schema
from scrape import scrape

# TESTING
if __name__ == "__main__":
    token_limit = 4000


    async def scrape_with_llm(url: str):

        html_content = await scrape(url)
        html_content_fits_content_window = html_content[:token_limit]

        extracted_content = await extract(html_content_fits_content_window, schema=airbnb_schema)
        print(extracted_content)
        print(extracted_content.count("listing_location"))


    url = "https://www.airbnb.co.uk/?checkin=2024-12-18&checkout=2024-12-27&adults=10&price_filter_num_nights=9&price_max=610&price_min=590"

    asyncio.run(scrape_with_llm(url))
