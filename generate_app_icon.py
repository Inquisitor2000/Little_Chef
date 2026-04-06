"""
Generate Android app icons from icon.jpg for all device densities
Creates both launcher icons and adaptive icons
"""
from PIL import Image
import os

# Input file
INPUT_FILE = "icon.jpg"

# Output directories
MIPMAP_DIRS = {
    "mdpi": "app/src/main/res/mipmap-mdpi",
    "hdpi": "app/src/main/res/mipmap-hdpi",
    "xhdpi": "app/src/main/res/mipmap-xhdpi",
    "xxhdpi": "app/src/main/res/mipmap-xxhdpi",
    "xxxhdpi": "app/src/main/res/mipmap-xxxhdpi"
}

# Icon sizes for each density
ICON_SIZES = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192
}

# Adaptive icon sizes (foreground layer)
ADAPTIVE_SIZES = {
    "mdpi": 108,
    "hdpi": 162,
    "xhdpi": 216,
    "xxhdpi": 324,
    "xxxhdpi": 432
}

def create_directories():
    """Create all necessary mipmap directories"""
    for dir_path in MIPMAP_DIRS.values():
        os.makedirs(dir_path, exist_ok=True)
        print(f"Created directory: {dir_path}")

def generate_launcher_icons(img):
    """Generate standard launcher icons for all densities"""
    print("\nGenerating launcher icons...")
    
    for density, size in ICON_SIZES.items():
        # Resize image
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        
        # Save as PNG
        output_path = os.path.join(MIPMAP_DIRS[density], "ic_launcher.png")
        resized.save(output_path, 'PNG')
        print(f"Created: {output_path} ({size}x{size})")

def generate_round_icons(img):
    """Generate round launcher icons for all densities"""
    print("\nGenerating round launcher icons...")
    
    for density, size in ICON_SIZES.items():
        # Create a new image with transparency
        round_img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        
        # Resize original image
        resized = img.resize((size, size), Image.Resampling.LANCZOS)
        
        # Create circular mask
        mask = Image.new('L', (size, size), 0)
        from PIL import ImageDraw
        draw = ImageDraw.Draw(mask)
        draw.ellipse((0, 0, size, size), fill=255)
        
        # Apply mask
        round_img.paste(resized, (0, 0))
        round_img.putalpha(mask)
        
        # Save as PNG
        output_path = os.path.join(MIPMAP_DIRS[density], "ic_launcher_round.png")
        round_img.save(output_path, 'PNG')
        print(f"Created: {output_path} ({size}x{size})")

def generate_adaptive_foreground(img):
    """Generate adaptive icon foreground layer for all densities"""
    print("\nGenerating adaptive icon foreground layers...")
    
    for density, size in ADAPTIVE_SIZES.items():
        # Adaptive icons: only center 66% is guaranteed visible (safe zone)
        # We need to scale the icon to fill more of the canvas
        # Scale factor: make the icon 85% of the 108dp canvas (instead of 66%)
        # This ensures it fills the visible area better while staying in safe zone
        
        scale_factor = 0.85  # Use 85% of the canvas
        icon_size = int(size * scale_factor)
        padding = (size - icon_size) // 2
        
        # Create canvas with transparency
        canvas = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        
        # Resize icon
        resized = img.resize((icon_size, icon_size), Image.Resampling.LANCZOS)
        
        # Paste icon centered on canvas
        canvas.paste(resized, (padding, padding), resized if resized.mode == 'RGBA' else None)
        
        # Save as PNG
        output_path = os.path.join(MIPMAP_DIRS[density], "ic_launcher_foreground.png")
        canvas.save(output_path, 'PNG')
        print(f"Created: {output_path} ({size}x{size}, icon: {icon_size}x{icon_size})")

def main():
    print("=" * 60)
    print("Android App Icon Generator")
    print("=" * 60)
    
    # Check if input file exists
    if not os.path.exists(INPUT_FILE):
        print(f"Error: {INPUT_FILE} not found!")
        return
    
    # Load the image
    print(f"\nLoading {INPUT_FILE}...")
    img = Image.open(INPUT_FILE)
    
    # Convert to RGBA if needed
    if img.mode != 'RGBA':
        img = img.convert('RGBA')
    
    print(f"Original size: {img.size}")
    
    # Create directories
    create_directories()
    
    # Generate all icon types
    generate_launcher_icons(img)
    generate_round_icons(img)
    generate_adaptive_foreground(img)
    
    print("\n" + "=" * 60)
    print("✓ All icons generated successfully!")
    print("=" * 60)
    print("\nGenerated icons:")
    print("  • ic_launcher.png (standard launcher icons)")
    print("  • ic_launcher_round.png (round launcher icons)")
    print("  • ic_launcher_foreground.png (adaptive icon foreground)")
    print("\nNote: Make sure you have ic_launcher_background.xml defined")
    print("      in your drawable folders for adaptive icons.")

if __name__ == "__main__":
    main()
