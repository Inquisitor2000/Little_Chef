#!/usr/bin/env python3
"""
Recipe Scraper for Family Recipe Planner App
Scrapes recipes from URLs and downloads images locally
"""

import json
import os
import re
import sys
from pathlib import Path
from typing import Optional
from urllib.parse import urlparse

try:
    import requests
    from bs4 import BeautifulSoup
    from dotenv import load_dotenv
    from playwright.sync_api import sync_playwright
except ImportError:
    print("Install packages: python -m pip install -r requirements.txt")
    print("Also run: playwright install chromium")
    sys.exit(1)

load_dotenv()

OPENAI_API_KEY = os.getenv('OPENAI_API_KEY')
OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"
CUISINES = [
    'Italian',
    'Mexican', 
    'Asian',
    'Mediterranean',
    'French',
    'Bread & Bakery',
    'Soups & Stews',
    'Vegetarian & Vegan',
    'Meat Dishes',
    'Desserts & Sweets'
]
MEAL_TYPES = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK', 'DESSERT']
DISH_CATEGORIES = [
    'PASTA', 'SALAD', 'SOUP', 'MAIN_COURSE', 'APPETIZER', 'SIDE_DISH', 
    'BREAD', 'SEAFOOD', 'CHICKEN', 'BEEF', 'PORK', 'VEGETARIAN', 
    'RICE_BOWL', 'SANDWICH', 'PIZZA', 'DESSERT', 'BEVERAGE', 'BAKED_DISH'
]
OUTPUT_FOLDER = "scraped_recipes"
ASSETS_FOLDER = "../app/src/main/assets/recipes"
IMAGES_FOLDER = os.path.join(ASSETS_FOLDER, "images")

PROMPT = """Extract recipe and SCALE TO 2 SERVINGS.

Units: g (solids), ml (liquids), pinch/tsp/Tbsp (spices), pcs (whole items)
Round: g/ml to nearest 10, tsp/Tbsp to 0.5

Meal Types: BREAKFAST, LUNCH, DINNER, SNACK, DESSERT

Dish Categories: PASTA, SALAD, SOUP, MAIN_COURSE, APPETIZER, SIDE_DISH, BREAD, SEAFOOD, CHICKEN, BEEF, PORK, VEGETARIAN, RICE_BOWL, SANDWICH, PIZZA, DESSERT, BEVERAGE, BAKED_DISH

Category Guidelines:
- Use CHICKEN/BEEF/PORK for dishes where that specific meat is the star ingredient
- Use SEAFOOD for fish, shrimp, shellfish dishes
- Use RICE_BOWL for rice-based bowls (poke, bibimbap, etc.)
- Use BAKED_DISH for casseroles, gratins, baked pasta
- Use MAIN_COURSE for other main dishes that don't fit specific categories
- Use VEGETARIAN for meatless dishes

For each ingredient, mark isStarIngredient=true for the 2-3 main ingredients that define the dish (e.g., protein, main carb, or signature ingredient).

Return JSON:
{"name":"","instructions":"1. Step...\\n\\n2. Step...","simpleInstructions":"1. Brief...\\n\\n2. Brief...","prepTimeMinutes":10,"cookTimeMinutes":20,"servings":2,"mealType":"DINNER","dishCategory":"MAIN_COURSE","ingredients":[{"name":"","quantity":100,"unit":"g","isStarIngredient":true}]}
"""


def fetch_webpage(url):
    """Fetch webpage using Playwright with stealth mode to bypass bot detection."""
    try:
        with sync_playwright() as p:
            # Launch browser in headless mode with stealth settings
            browser = p.chromium.launch(
                headless=True,
                args=[
                    '--disable-blink-features=AutomationControlled',
                    '--disable-dev-shm-usage',
                    '--no-sandbox',
                    '--disable-setuid-sandbox',
                ]
            )
            
            # Create context with realistic settings
            context = browser.new_context(
                viewport={'width': 1920, 'height': 1080},
                user_agent='Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                locale='en-US',
                timezone_id='America/New_York',
            )
            
            # Add stealth scripts to hide automation
            context.add_init_script("""
                Object.defineProperty(navigator, 'webdriver', {
                    get: () => undefined
                });
                
                window.chrome = {
                    runtime: {}
                };
                
                Object.defineProperty(navigator, 'plugins', {
                    get: () => [1, 2, 3, 4, 5]
                });
                
                Object.defineProperty(navigator, 'languages', {
                    get: () => ['en-US', 'en']
                });
            """)
            
            page = context.new_page()
            
            # Navigate with realistic behavior
            print(f"  Loading page...")
            page.goto(url, wait_until='domcontentloaded', timeout=30000)
            
            # Wait a bit for dynamic content
            page.wait_for_timeout(2000)
            
            # Get the HTML content
            html = page.content()
            
            browser.close()
            return html
            
    except Exception as e:
        print(f"  Error: {e}")
        return None


def clean_html(html):
    soup = BeautifulSoup(html, 'html.parser')
    for tag in soup(['script', 'style', 'nav', 'footer', 'header']):
        tag.decompose()
    return re.sub(r'\s+', ' ', soup.get_text(' ', strip=True))[:12000]


def extract_images(html, base_url):
    soup = BeautifulSoup(html, 'html.parser')
    images = []
    for sel in ['img[itemprop="image"]', 'meta[property="og:image"]', 'article img']:
        for el in soup.select(sel):
            src = el.get('src') or el.get('content')
            if src and 'logo' not in src.lower() and 'icon' not in src.lower():
                if src.startswith('//'):
                    src = 'https:' + src
                elif src.startswith('/'):
                    p = urlparse(base_url)
                    src = f"{p.scheme}://{p.netloc}{src}"
                images.append(src)
    return list(dict.fromkeys(images))[:3]


def extract_slug(url):
    path = urlparse(url).path.rstrip('/')
    slug = path.split('/')[-1]
    slug = re.sub(r'-\d+$', '', slug)
    slug = re.sub(r'-recipe$', '', slug)
    return slug.replace('-', '_')


def download_image(url, output_path):
    """Download an image from URL and save to output_path."""
    try:
        headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
            'Accept': 'image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8',
            'Accept-Language': 'en-US,en;q=0.9',
            'Referer': 'https://www.allrecipes.com/',
        }
        response = requests.get(url, headers=headers, timeout=30)
        response.raise_for_status()
        
        output_path.parent.mkdir(parents=True, exist_ok=True)
        with open(output_path, 'wb') as f:
            f.write(response.content)
        
        file_size = output_path.stat().st_size / 1024  # KB
        print(f"  ✓ Downloaded image ({file_size:.1f} KB)")
        return True
    except Exception as e:
        print(f"  ✗ Failed to download image: {e}")
        return False


def save_image_locally(image_url, recipe_id, cuisine):
    """Download image and return local path for assets."""
    if not image_url:
        return None
    
    # Determine file extension
    ext = '.jpg'
    if '.png' in image_url.lower():
        ext = '.png'
    
    # Create local path
    cuisine_lower = cuisine.lower()
    filename = f"{recipe_id}{ext}"
    local_path = Path(IMAGES_FOLDER) / cuisine_lower / filename
    
    # Download image
    print(f"  Downloading image...")
    if download_image(image_url, local_path):
        # Return path relative to assets folder for JSON
        return f"recipes/images/{cuisine_lower}/{filename}"
    
    return None


def scrape_with_openai(url, html):
    if not OPENAI_API_KEY:
        print("  Error: No API key")
        return None
    
    text = clean_html(html)
    images = extract_images(html, url)
    
    try:
        r = requests.post(
            OPENAI_API_URL,
            headers={'Authorization': f'Bearer {OPENAI_API_KEY}', 'Content-Type': 'application/json'},
            json={
                'model': 'gpt-4o',
                'messages': [{'role': 'user', 'content': f"{PROMPT}\n\nURL: {url}\n\nContent:\n{text}"}],
                'max_tokens': 3000,
                'response_format': {'type': 'json_object'}
            },
            timeout=30
        )
        r.raise_for_status()
        data = json.loads(r.json()['choices'][0]['message']['content'])
        data['sourceUrl'] = url
        data['imageUrlOriginal'] = images[0] if images else None  # Store original URL temporarily
        return data
    except Exception as e:
        print(f"  API Error: {e}")
        return None


def select_cuisine():
    """Let user select cuisine from available options."""
    print("\n" + "=" * 50)
    print("  SELECT CUISINE")
    print("=" * 50)
    for i, c in enumerate(CUISINES, 1):
        print(f"    {i}. {c}")
    
    while True:
        try:
            choice = input(f"\n  Enter number (1-{len(CUISINES)}): ").strip()
            idx = int(choice) - 1
            if 0 <= idx < len(CUISINES):
                selected = CUISINES[idx]
                print(f"  ✓ Selected: {selected}")
                return selected
        except (ValueError, IndexError):
            pass
        print(f"  ✗ Invalid choice. Please enter 1-{len(CUISINES)}")


def select_meal_type(current_value):
    """Let user select or confirm meal type."""
    print("\n" + "=" * 50)
    print("  SELECT MEAL TYPE")
    print("=" * 50)
    print(f"  AI suggested: {current_value}")
    print("\n  Available options:")
    for i, mt in enumerate(MEAL_TYPES, 1):
        marker = "✓" if mt == current_value else " "
        print(f"    {i}. {mt} {marker}")
    
    choice = input(f"\n  Press Enter to keep '{current_value}' or enter number (1-{len(MEAL_TYPES)}): ").strip()
    
    if not choice:
        print(f"  ✓ Keeping: {current_value}")
        return current_value
    
    try:
        idx = int(choice) - 1
        if 0 <= idx < len(MEAL_TYPES):
            selected = MEAL_TYPES[idx]
            print(f"  ✓ Changed to: {selected}")
            return selected
    except (ValueError, IndexError):
        pass
    
    print(f"  ✗ Invalid choice, keeping: {current_value}")
    return current_value


def select_dish_category(current_value):
    """Let user select or confirm dish category."""
    print("\n" + "=" * 50)
    print("  SELECT DISH CATEGORY")
    print("=" * 50)
    print(f"  AI suggested: {current_value}")
    print("\n  Available options:")
    
    # Display in columns for better readability
    cols = 3
    for i in range(0, len(DISH_CATEGORIES), cols):
        row = []
        for j in range(cols):
            idx = i + j
            if idx < len(DISH_CATEGORIES):
                cat = DISH_CATEGORIES[idx]
                marker = "✓" if cat == current_value else " "
                row.append(f"{idx+1:2}. {cat:15} {marker}")
        print("    " + "  ".join(row))
    
    choice = input(f"\n  Press Enter to keep '{current_value}' or enter number (1-{len(DISH_CATEGORIES)}): ").strip()
    
    if not choice:
        print(f"  ✓ Keeping: {current_value}")
        return current_value
    
    try:
        idx = int(choice) - 1
        if 0 <= idx < len(DISH_CATEGORIES):
            selected = DISH_CATEGORIES[idx]
            print(f"  ✓ Changed to: {selected}")
            return selected
    except (ValueError, IndexError):
        pass
    
    print(f"  ✗ Invalid choice, keeping: {current_value}")
    return current_value


def save_recipe(data, cuisine, url):
    folder = os.path.join(OUTPUT_FOLDER, cuisine.lower())
    os.makedirs(folder, exist_ok=True)
    slug = extract_slug(url)
    path = os.path.join(folder, f"{slug}_{cuisine.lower()}.json")
    
    # Set recipe metadata
    data['cuisine'] = cuisine
    data['id'] = slug
    
    # Download image and update path to local
    if 'imageUrlOriginal' in data and data['imageUrlOriginal']:
        local_image_path = save_image_locally(data['imageUrlOriginal'], slug, cuisine)
        if local_image_path:
            data['imageUrl'] = local_image_path
        else:
            # Keep original URL as fallback
            data['imageUrl'] = data['imageUrlOriginal']
        del data['imageUrlOriginal']
    
    with open(path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    return path


def download_bundled_images():
    """Download images for existing bundled recipes in assets folder."""
    print("\n" + "=" * 50)
    print("  Downloading Bundled Recipe Images")
    print("=" * 50 + "\n")
    
    bundled_recipes_path = Path(ASSETS_FOLDER)
    if not bundled_recipes_path.exists():
        print(f"Error: Assets folder not found at {ASSETS_FOLDER}")
        return
    
    total = 0
    success = 0
    
    # Scan for recipe JSON files in assets
    for cuisine_folder in bundled_recipes_path.iterdir():
        if not cuisine_folder.is_dir() or cuisine_folder.name == 'images':
            continue
        
        cuisine_name = cuisine_folder.name
        print(f"\n{cuisine_name.upper()} RECIPES:")
        print("-" * 50)
        
        for recipe_file in cuisine_folder.glob('*.json'):
            try:
                with open(recipe_file, 'r', encoding='utf-8') as f:
                    recipe_data = json.load(f)
                
                recipe_id = recipe_data.get('id')
                recipe_name = recipe_data.get('name', 'Unknown')
                image_url = recipe_data.get('imageUrl', '')
                
                # Skip if already using local path
                if image_url.startswith('recipes/images/'):
                    print(f"  ✓ {recipe_name} - already using local image")
                    continue
                
                # Skip if no image URL
                if not image_url or not image_url.startswith('http'):
                    print(f"  ⊘ {recipe_name} - no image URL")
                    continue
                
                total += 1
                print(f"  Downloading: {recipe_name}")
                
                # Download image
                local_path = save_image_locally(image_url, recipe_id, cuisine_name.capitalize())
                
                if local_path:
                    # Update JSON file with local path
                    recipe_data['imageUrl'] = local_path
                    with open(recipe_file, 'w', encoding='utf-8') as f:
                        json.dump(recipe_data, f, indent=2, ensure_ascii=False)
                    print(f"  ✓ Updated {recipe_file.name}")
                    success += 1
                else:
                    print(f"  ✗ Failed to download image for {recipe_name}")
                    
            except Exception as e:
                print(f"  ✗ Error processing {recipe_file.name}: {e}")
    
    print("\n" + "=" * 50)
    print(f"Download complete: {success}/{total} images downloaded")
    if success < total:
        print(f"⚠ {total - success} images failed to download")


def main():
    print("\n" + "=" * 50)
    print("  Recipe Scraper")
    print("=" * 50)
    
    # Check for special commands
    if len(sys.argv) > 1:
        if sys.argv[1] in ['--download-images', '-d']:
            download_bundled_images()
            return
        elif sys.argv[1] in ['--help', '-h']:
            print("\nUsage:")
            print("  python recipe_scraper.py              # Interactive scraping mode")
            print("  python recipe_scraper.py -d           # Download images for bundled recipes")
            print("  python recipe_scraper.py --help       # Show this help")
            return
    
    if not OPENAI_API_KEY:
        print("\nError: Set OPENAI_API_KEY in .env file")
        sys.exit(1)
    
    print("\nPaste URL and press Enter. Type 'q' to quit.\n")
    
    while True:
        try:
            url = input("URL: ").strip()
        except (KeyboardInterrupt, EOFError):
            break
        
        if not url:
            continue
        if url.lower() in ['q', 'quit', 'exit']:
            break
        if not url.startswith('http'):
            url = 'https://' + url
        
        print(f"\nScraping...")
        html = fetch_webpage(url)
        if not html:
            print("  Failed to fetch page\n")
            continue
        
        print("  Extracting with AI...")
        data = scrape_with_openai(url, html)
        
        if data:
            print(f"\n  ✓ Found: {data.get('name')}")
            print(f"  ✓ Ingredients: {len(data.get('ingredients', []))}")
            print(f"  ✓ Prep: {data.get('prepTimeMinutes')}min | Cook: {data.get('cookTimeMinutes')}min")
            
            # Let user select/confirm meal type
            current_meal_type = data.get('mealType', 'DINNER')
            data['mealType'] = select_meal_type(current_meal_type)
            
            # Let user select/confirm dish category
            current_category = data.get('dishCategory', 'MAIN_COURSE')
            data['dishCategory'] = select_dish_category(current_category)
            
            # Let user select cuisine
            cuisine = select_cuisine()
            
            # Save recipe
            path = save_recipe(data, cuisine, url)
            print(f"\n  ✓ Saved: {path}\n")
        else:
            print("  ✗ Failed to extract recipe\n")
        
        print("-" * 50 + "\n")
    
    print("\nGoodbye!")


if __name__ == '__main__':
    main()
