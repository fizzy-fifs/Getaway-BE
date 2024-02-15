from langchain_community.document_loaders import AsyncChromiumLoader
from langchain_community.document_transformers import BeautifulSoupTransformer

# TESTING
if __name__ == "__main__":
    token_limit = 4000
    loader = AsyncChromiumLoader([
        "https://www.airbnb.co.uk/?checkin=2024-12-18&checkout=2024-12-27&adults=10&price_filter_num_nights=9&price_max=610&price_min=590"])
    html = loader.load()
