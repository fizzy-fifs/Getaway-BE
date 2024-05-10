from bs4 import BeautifulSoup
from langchain_core.tools import BaseTool
import requests


class AccommodationWebPageTool(BaseTool):
    name = "Get Information on Accommodations"
    description = "Useful for when you want to get information on accommodations that fit a specific set of criteria"

    def _run(self, url: str):
        response = requests.get(url)
        html_content = response.text

        soup = BeautifulSoup(html_content, "html.parser")
        return soup.get_text()
