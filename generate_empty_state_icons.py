"""
Generate flat empty state icons for the Family Meal Planner app.
Using emoji style to match the flat ingredient icons.
"""

from PIL import Image, ImageDraw, ImageFont
import os

# Create output directory
output_dir = "app/src/main/res/drawable"
os.makedirs(output_dir, exist_ok=True)

# Icon size
SIZE = 256
CENTER = SIZE // 2

def create_icon(filename, emoji):
    """Create a flat icon with just the emoji (matching ingredient icon style)"""
    
    # Create image with transparency
    img = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Load emoji font
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
                font = ImageFont.truetype(path, 200)
                break
        
        if font is None:
            print(f"Warning: Could not find emoji font, using default for {filename}")
            font = ImageFont.load_default()
    except Exception as e:
        print(f"Error loading font for {filename}: {e}")
        font = ImageFont.load_default()
    
    # Draw emoji centered with proper bounding box calculation
    bbox = draw.textbbox((0, 0), emoji, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    
    # Calculate position to center the emoji
    x = CENTER - text_width // 2 - bbox[0]
    y = CENTER - text_height // 2 - bbox[1]
    
    draw.text((x, y), emoji, font=font, embedded_color=True)
    
    # Save as PNG
    output_path = os.path.join(output_dir, filename)
    img.save(output_path, 'PNG')
    print(f"✓ Created {filename}")

def create_pantry_shelf_icon(filename):
    """Create a flat pantry cabinet icon that works in both light and dark themes"""
    
    # Create image with transparency
    img = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Colors - using neutral tones that work on both themes
    cabinet_frame = (120, 80, 50)      # Medium brown for cabinet frame
    cabinet_back = (240, 235, 225)     # Light beige for interior
    shelf_color = (160, 110, 70)       # Lighter brown for shelves
    shelf_shadow = (100, 70, 45)       # Shadow under shelves
    door_color = (139, 90, 60)         # Warm brown for doors
    door_edge = (101, 67, 33)          # Darker brown for door panels
    outline_color = (70, 45, 20)       # Dark outline
    handle_color = (180, 160, 120)     # Brass/gold for handles
    
    # Door dimensions (doors are the reference size)
    door_width = 85
    door_height = 170
    door_top = (SIZE - door_height) // 2
    door_bottom = door_top + door_height
    
    # Cabinet interior matches door dimensions exactly
    cabinet_left = (SIZE - door_width * 2) // 2
    cabinet_right = cabinet_left + (door_width * 2)
    cabinet_top = door_top
    cabinet_bottom = door_bottom
    cabinet_width = cabinet_right - cabinet_left
    cabinet_height = cabinet_bottom - cabinet_top
    
    # Draw cabinet frame (outer box) - same size as door area
    frame_thickness = 8
    draw.rectangle(
        [cabinet_left, cabinet_top, cabinet_right, cabinet_bottom],
        fill=cabinet_frame,
        outline=outline_color,
        width=3
    )
    
    # Draw interior/back wall - same size as door area
    draw.rectangle(
        [cabinet_left + frame_thickness, cabinet_top + frame_thickness, 
         cabinet_right - frame_thickness, cabinet_bottom - frame_thickness],
        fill=cabinet_back,
        outline=None
    )
    
    # Draw shelves inside the cabinet
    shelf_height = 8
    interior_top = cabinet_top + frame_thickness + 10
    interior_bottom = cabinet_bottom - frame_thickness - 10
    shelf_spacing = (interior_bottom - interior_top) // 4
    
    for i in range(1, 4):  # 3 shelves
        y = interior_top + (i * shelf_spacing)
        
        # Shelf shadow (underneath)
        draw.rectangle(
            [cabinet_left + frame_thickness + 5, y + shelf_height, 
             cabinet_right - frame_thickness - 5, y + shelf_height + 3],
            fill=shelf_shadow,
            outline=None
        )
        
        # Main shelf
        draw.rectangle(
            [cabinet_left + frame_thickness + 5, y, 
             cabinet_right - frame_thickness - 5, y + shelf_height],
            fill=shelf_color,
            outline=outline_color,
            width=1
        )
    
    # Draw open doors (partially open to show interior)
    # Left door (slightly open, angled) - matches cabinet height
    left_door_points = [
        (cabinet_left - 20, cabinet_top + 5),  # Top left (extended out)
        (cabinet_left + door_width - 5, cabinet_top),  # Top right
        (cabinet_left + door_width - 5, cabinet_bottom),  # Bottom right
        (cabinet_left - 20, cabinet_bottom - 5)  # Bottom left (extended out)
    ]
    draw.polygon(left_door_points, fill=door_color, outline=outline_color, width=3)
    
    # Left door panel detail (inset rectangle)
    draw.polygon([
        (cabinet_left - 15, cabinet_top + 20),
        (cabinet_left + door_width - 15, cabinet_top + 15),
        (cabinet_left + door_width - 15, cabinet_bottom - 15),
        (cabinet_left - 15, cabinet_bottom - 20)
    ], fill=None, outline=door_edge, width=2)
    
    # Left door handle
    draw.ellipse(
        [cabinet_left + door_width - 25, CENTER - 8, 
         cabinet_left + door_width - 15, CENTER + 8],
        fill=handle_color,
        outline=outline_color,
        width=2
    )
    
    # Right door (slightly open, angled) - matches cabinet height
    right_door_points = [
        (cabinet_right - door_width + 5, cabinet_top),  # Top left
        (cabinet_right + 20, cabinet_top + 5),  # Top right (extended out)
        (cabinet_right + 20, cabinet_bottom - 5),  # Bottom right (extended out)
        (cabinet_right - door_width + 5, cabinet_bottom)  # Bottom left
    ]
    draw.polygon(right_door_points, fill=door_color, outline=outline_color, width=3)
    
    # Right door panel detail (inset rectangle)
    draw.polygon([
        (cabinet_right - door_width + 15, cabinet_top + 15),
        (cabinet_right + 15, cabinet_top + 20),
        (cabinet_right + 15, cabinet_bottom - 20),
        (cabinet_right - door_width + 15, cabinet_bottom - 15)
    ], fill=None, outline=door_edge, width=2)
    
    # Right door handle
    draw.ellipse(
        [cabinet_right - door_width + 15, CENTER - 8, 
         cabinet_right - door_width + 25, CENTER + 8],
        fill=handle_color,
        outline=outline_color,
        width=2
    )
    
    # Add some depth lines on the frame
    draw.line(
        [(cabinet_left + 3, cabinet_top + 3), (cabinet_left + 3, cabinet_bottom - 3)],
        fill=outline_color,
        width=1
    )
    draw.line(
        [(cabinet_right - 3, cabinet_top + 3), (cabinet_right - 3, cabinet_bottom - 3)],
        fill=outline_color,
        width=1
    )
    
    # Save as PNG
    output_path = os.path.join(output_dir, filename)
    img.save(output_path, 'PNG')
    print(f"✓ Created {filename} (custom pantry cabinet design)")





if __name__ == "__main__":
    print("Generating flat empty state icons...")
    print()
    
    # Empty state icons with appropriate emojis
    create_icon("ic_empty_groceries.png", "🛒")
    create_icon("ic_empty_suggestions.png", "💡")
    create_icon("ic_empty_meals.png", "🍽️")
    create_pantry_shelf_icon("ic_empty_pantry.png")  # Custom pantry shelf design
    create_icon("ic_empty_plan.png", "📅")
    create_icon("ic_empty_recipes.png", "📖")
    
    print()
    print("✅ All icons generated successfully!")
    print(f"Icons saved to: {output_dir}")
