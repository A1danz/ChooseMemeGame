from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
root = next(Path("classes").rglob("start-page-view.fxml")).parent
faces = "😎 🤔 🥲 🙃 😱 🤡 🫠 🧐 😴 🥳 😬 🤯 😤 🫣 😵 🤑 😈 🙄 🥶 🥵 🥺 😇 🤨 🤫 🤐 🤠 😮 🤭 😅 😂 😭 😍 🥸 😏 😒 🤓 😳 🤷 🐈 🐕 🦥 🐸 🦄 🦖 🐼 🦊 🐧 🐵".split()
font = ImageFont.truetype("/usr/share/fonts/truetype/noto/NotoColorEmoji.ttf", 109)
def tile(path, symbol, bg="#eef2ff"):
    target = root / path
    if target.exists(): return
    target.parent.mkdir(parents=True, exist_ok=True)
    img = Image.new("RGB", (180,180), bg)
    ImageDraw.Draw(img).text((90,90), symbol, font=font, anchor="mm", embedded_color=True)
    img.save(target)
for i,face in enumerate(faces):
    tile(f"memes/default/{89+i}.png",face)
for name,face in zip("1 2 anaconda armadillo bear bird butterfly deer ganesha jacutinga jaguar turtle".split(),"🐱 🐶 🐍 🦔 🐻 🐦 🦋 🦌 🐘 🦜 🐆 🐢".split()):
    tile(f"img/avatars/default/{name}.png",face)
for name,face in zip("background game_field logo MEME forbidden refresh plus rabbit cards_stack instruction waiting_players".split(),["","","😂","🎴","🚫","🔄","➕","🐰","🎴","🃏","⌛"]):
    tile(f"img/{name}.png",face,"#d7cdf6")
for i,number in enumerate([1,100,102,108,13,2,21,26,32,39,45,60,62,86]):
    tile(f"memes/default/{number}.jpg",faces[-1-i])
for name in ["loading","loading_small"]:
    tile(f"img/{name}.gif","⌛")
