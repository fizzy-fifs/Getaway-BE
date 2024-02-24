from urllib.parse import urlparse

from bs4 import BeautifulSoup
from playwright.async_api import async_playwright


async def scrape(url: str):
    """
    Async function to scrape content from a given URL
    :param url:
    :return:
    """
    parsed_url = urlparse(url)
    print(f"Scraping content from {parsed_url.netloc}...")
    async with async_playwright() as p:
        browser = await p.chromium.launch(headless=True)

        try:
            page = await browser.new_page()
            await page.goto(url)
            html = await page.content()

            soup = remove_unwanted_tags(html)

            content = extract_wanted_tags(soup)

            results = remove_unnecessary_lines(content)

            print("Scraping complete!")
        except Exception as e:
            results = f"An error occurred: {e}"
            print(results)
        await browser.close()
        return results


def remove_unnecessary_lines(content: str):
    # Split content into lines
    lines = content.split("\n")

    # Strip whitespace from each line
    stripped_lines = [line.strip() for line in lines]

    # Fill out empty lines
    non_empty_lines = [line for line in stripped_lines if line]

    # Remove duplicated lines (while preserving order)
    seen = set()
    deduped_lines = [line for line in non_empty_lines if not (
            line in seen or seen.add(line))]

    return "".join(deduped_lines)


def extract_wanted_tags(soup: BeautifulSoup, wanted_tags: list = ["span", "h1", "h2", "h3"]):
    text_parts = []
    for tag in wanted_tags:
        for element in soup.find_all(tag):
            if element == 'a':
                href = element.get('href')
                if href:
                    text_parts.append(f"{element.text} ({href})")
                else:
                    text_parts.append(element.text)
            else:
                text_parts.append(element.text)
    return " ".join(text_parts)


def remove_unwanted_tags(html: str, unwanted_tags: list = ['script', 'style', 'head', 'title', 'meta', '[document]']):
    soup = BeautifulSoup(html, 'html.parser')

    for tag in unwanted_tags:
        for element in soup.find_all(tag):
            element.extract()

    return soup
