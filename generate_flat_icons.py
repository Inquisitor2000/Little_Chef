"""
Generate 92 flat icons for categories and subcategories (no circular background)
Icons are larger and more diverse to avoid repetition
"""
from PIL import Image, ImageDraw, ImageFont
import os

# Configuration
SIZE = 256
CENTER = SIZE // 2
OUTPUT_DIR = "app/src/main/res/drawable"

def hex_to_rgb(hex_color):
    """Convert hex color to RGB tuple"""
    hex_color = hex_color.lstrip('#')
    return tuple(int(hex_color[i:i+2], 16) for i in (0, 2, 4))

def create_icon(filename, emoji, color_hex):
    """Create a flat icon with just the emoji (no background circle)"""
    
    # Create image with transparency
    img = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Load emoji font (system emoji font) - INCREASED SIZE for better coverage
    try:
        # Try different emoji font paths for different systems
        font_paths = [
            "/System/Library/Fonts/Apple Color Emoji.ttc",  # macOS
            "C:\\Windows\\Fonts\\seguiemj.ttf",  # Windows
            "/usr/share/fonts/truetype/noto/NotoColorEmoji.ttf",  # Linux
        ]
        
        font = None
        for path in font_paths:
            if os.path.exists(path):
                # Reduced from 220 to 200 to add padding and prevent cutoff
                font = ImageFont.truetype(path, 200)
                break
        
        if font is None:
            print(f"Warning: Could not find emoji font, using default for {filename}")
            font = ImageFont.load_default()
    except Exception as e:
        print(f"Error loading font for {filename}: {e}")
        font = ImageFont.load_default()
    
    # Draw emoji centered with proper bounding box calculation
    # Get the bounding box of the text
    bbox = draw.textbbox((0, 0), emoji, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    
    # Calculate position to center the emoji
    # Account for the bbox offset (bbox[0] and bbox[1])
    x = CENTER - text_width // 2 - bbox[0]
    y = CENTER - text_height // 2 - bbox[1]
    
    draw.text((x, y), emoji, font=font, embedded_color=True)
    
    # Save as PNG
    output_path = os.path.join(OUTPUT_DIR, filename)
    img.save(output_path, 'PNG')
    print(f"Created: {output_path}")

# Icon definitions - MORE DIVERSE EMOJIS, NO REPETITION
icons = [
    # Categories (15 main categories)
    ("ic_cat_meat_poultry.png", "🍗", "#E57373"),
    ("ic_cat_seafood.png", "🦞", "#4FC3F7"),
    ("ic_cat_dairy_eggs.png", "🧀", "#FFD54F"),
    ("ic_cat_vegetables.png", "🥕", "#81C784"),
    ("ic_cat_fruits.png", "🍓", "#FF8A65"),
    ("ic_cat_grains_bread.png", "🍞", "#FFB74D"),
    ("ic_cat_legumes_beans.png", "🫘", "#A1887F"),
    ("ic_cat_nuts_seeds.png", "🌰", "#BCAAA4"),
    ("ic_cat_oils_fats.png", "🫒", "#FFF176"),
    ("ic_cat_spices_herbs.png", "🌶️", "#AED581"),
    ("ic_cat_condiments_sauces.png", "🍯", "#FFB74D"),
    ("ic_cat_sweeteners_baking.png", "🧁", "#F48FB1"),
    ("ic_cat_canned_preserved.png", "🥫", "#90A4AE"),
    ("ic_cat_beverages.png", "☕", "#8D6E63"),
    ("ic_cat_snacks.png", "🍿", "#FFD54F"),
    
    # Meat & Poultry Subcategories (7)
    ("ic_sub_chicken.png", "🐔", "#FFB74D"),
    ("ic_sub_beef.png", "🥩", "#E57373"),
    ("ic_sub_pork.png", "🐷", "#F48FB1"),
    ("ic_sub_lamb.png", "🐑", "#CE93D8"),
    ("ic_sub_turkey.png", "🦃", "#BCAAA4"),
    ("ic_sub_duck.png", "🦆", "#A1887F"),
    ("ic_sub_processed.png", "🌭", "#FF8A65"),
    
    # Seafood Subcategories (3)
    ("ic_sub_fish.png", "🐟", "#4FC3F7"),
    ("ic_sub_shellfish.png", "🦐", "#80DEEA"),
    ("ic_sub_cephalopods.png", "🦑", "#4DD0E1"),
    
    # Dairy & Eggs Subcategories (5)
    ("ic_sub_milk.png", "🥛", "#FFD54F"),
    ("ic_sub_cheese.png", "🧀", "#FFB74D"),
    ("ic_sub_yogurt.png", "🍦", "#FFF176"),
    ("ic_sub_butter.png", "🧈", "#FFEB3B"),
    ("ic_sub_eggs.png", "🥚", "#FFF9C4"),
    ("ic_sub_cultured_dairy.png", "🫙", "#FFE082"),
    
    # Vegetables Subcategories (7)
    ("ic_sub_leafy_greens.png", "🥬", "#81C784"),
    ("ic_sub_root_vegetables.png", "🥕", "#FFB74D"),
    ("ic_sub_nightshades.png", "🍅", "#E57373"),
    ("ic_sub_cruciferous.png", "🥦", "#66BB6A"),
    ("ic_sub_squash.png", "🎃", "#FFB74D"),
    ("ic_sub_alliums.png", "🧅", "#D4E157"),
    ("ic_sub_mushrooms.png", "🍄", "#BCAAA4"),
    
    # Fruits Subcategories (6)
    ("ic_sub_citrus.png", "🍊", "#FFB74D"),
    ("ic_sub_berries.png", "🫐", "#9575CD"),
    ("ic_sub_stone_fruits.png", "🍑", "#FFAB91"),
    ("ic_sub_pome_fruits.png", "🍎", "#E57373"),
    ("ic_sub_tropical.png", "🥥", "#FFD54F"),
    ("ic_sub_melons.png", "🍉", "#EF5350"),
    
    # Grains & Bread Subcategories (6)
    ("ic_sub_bread.png", "🥖", "#FFB74D"),
    ("ic_sub_rice.png", "🍚", "#FFF9C4"),
    ("ic_sub_pasta.png", "🍝", "#FFD54F"),
    ("ic_sub_cereals.png", "🥣", "#FFCC80"),
    ("ic_sub_flour.png", "🌾", "#D7CCC8"),
    ("ic_sub_other_grains.png", "🌽", "#BCAAA4"),
    
    # Legumes & Beans Subcategories (5)
    ("ic_sub_beans.png", "🫘", "#A1887F"),
    ("ic_sub_lentils.png", "🍛", "#8D6E63"),
    ("ic_sub_peas.png", "🫛", "#AED581"),
    ("ic_sub_soy_products.png", "🥡", "#9E9D24"),
    ("ic_sub_peanuts.png", "🥜", "#BCAAA4"),
    
    # Nuts & Seeds Subcategories (3)
    ("ic_sub_tree_nuts.png", "🌰", "#8D6E63"),
    ("ic_sub_seeds.png", "🌻", "#FFB74D"),
    ("ic_sub_nut_butters.png", "🥜", "#A1887F"),
    
    # Oils & Fats Subcategories (3)
    ("ic_sub_cooking_oils.png", "🫒", "#FFF176"),
    ("ic_sub_specialty_oils.png", "🌻", "#FFEB3B"),
    ("ic_sub_animal_fats.png", "🧈", "#FFD54F"),
    
    # Spices & Herbs Subcategories (5)
    ("ic_sub_dried_herbs.png", "🌿", "#AED581"),
    ("ic_sub_fresh_herbs.png", "🪴", "#81C784"),
    ("ic_sub_ground_spices.png", "🌶️", "#FF8A65"),
    ("ic_sub_whole_spices.png", "⭐", "#FFB74D"),
    ("ic_sub_salt_basics.png", "🧂", "#ECEFF1"),
    
    # Condiments & Sauces Subcategories (5)
    ("ic_sub_western_sauces.png", "🍅", "#FFB74D"),
    ("ic_sub_asian_sauces.png", "🥢", "#8D6E63"),
    ("ic_sub_vinegars.png", "🫙", "#FFF176"),
    ("ic_sub_spreads.png", "🥪", "#BCAAA4"),
    ("ic_sub_fermented_pastes.png", "🍶", "#A1887F"),
    
    # Sweeteners & Baking Subcategories (4)
    ("ic_sub_sugars.png", "🍬", "#F48FB1"),
    ("ic_sub_liquid_sweeteners.png", "🍯", "#FFD54F"),
    ("ic_sub_baking_essentials.png", "🥐", "#FFB74D"),
    ("ic_sub_baking_decorating.png", "🎂", "#F48FB1"),
    
    # Canned & Preserved Subcategories (6)
    ("ic_sub_canned_vegetables.png", "🥫", "#81C784"),
    ("ic_sub_canned_fruits.png", "🍑", "#FFB74D"),
    ("ic_sub_canned_beans.png", "🥘", "#A1887F"),
    ("ic_sub_canned_fish.png", "🐟", "#4FC3F7"),
    ("ic_sub_pickled.png", "🥒", "#AED581"),
    ("ic_sub_broths_stocks.png", "🍲", "#FFB74D"),
    
    # Beverages Subcategories (6)
    ("ic_sub_water_basic.png", "💧", "#4FC3F7"),
    ("ic_sub_coffee_tea.png", "☕", "#8D6E63"),
    ("ic_sub_juices.png", "🧃", "#FFB74D"),
    ("ic_sub_soft_drinks.png", "🥤", "#E57373"),
    ("ic_sub_plant_milks.png", "🥛", "#FFF9C4"),
    ("ic_sub_cooking_liquids.png", "🍶", "#90A4AE"),
    
    # Snacks Subcategories (4)
    ("ic_sub_chips_crackers.png", "🥨", "#FFD54F"),
    ("ic_sub_bars_cookies.png", "🍪", "#FFB74D"),
    ("ic_sub_chocolate_candy.png", "🍫", "#8D6E63"),
    ("ic_sub_dried_snacks.png", "🍇", "#BCAAA4"),
    
    # Other
    ("ic_sub_other.png", "📦", "#90A4AE"),
]

def main():
    print(f"Generating {len(icons)} flat icons...")
    
    # Create output directory if it doesn't exist
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    
    # Generate all icons
    for filename, emoji, color in icons:
        create_icon(filename, emoji, color)
    
    print(f"\nSuccessfully generated {len(icons)} flat icons!")
    print(f"Icons saved to: {OUTPUT_DIR}")

if __name__ == "__main__":
    main()
