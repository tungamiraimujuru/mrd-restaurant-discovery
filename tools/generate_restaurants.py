#!/usr/bin/env python3
"""Generates the mock restaurant dataset served by the fake backend.

Deterministic (fixed seed), so the committed JSON can be reproduced exactly:
    python3 tools/generate_restaurants.py
"""
import json
import random
from pathlib import Path

SEED = 20261101
TOTAL_VALID = 250
OUTPUT = Path(__file__).resolve().parent.parent / "core/network/src/main/assets/restaurants.json"

SUBURBS = [
    "Sea Point", "Gardens", "Camps Bay", "Claremont", "Woodstock", "Green Point",
    "Observatory", "Newlands", "Constantia", "Muizenberg", "Bellville", "Durbanville",
]

GROUPS = [
    (["Steakhouse", "Grill"], ["Cape Town Grill", "The Karoo Grill", "Braai Master", "Flame & Fire", "Prime Cut", "Smokehouse"]),
    (["Sushi", "Japanese"], ["Sushi on", "Tokyo Table", "Sakura", "Wasabi", "Ocean Roll", "Nori"]),
    (["Pizza", "Italian"], ["Bella Italia", "Napoli Wood Fire", "Slice of Rome", "Il Forno", "Pasta Fresca", "Luigi's"]),
    (["Burgers", "American"], ["Smash House", "Big Stack", "Patty Co", "Burger Joint", "Bun & Beef", "Diner 24"]),
    (["Indian", "Curry"], ["Spice Route", "Taj Kitchen", "Masala Bay", "Delhi Darbar", "Curry Leaf"]),
    (["Traditional", "African"], ["Mzansi Soul Kitchen", "Ubuntu Table", "Kasi Kitchen", "Mama Africa", "Gogo's Pot"]),
    (["Chicken", "Peri-Peri"], ["Peri Palace", "Flame Chicken", "Piri Piri House", "Chicken Shack", "Coop & Co"]),
    (["Chinese", "Asian"], ["Golden Dragon", "Wok This Way", "Jade Garden", "Lucky Noodle", "Bamboo House"]),
    (["Seafood", "Mediterranean"], ["Catch of the Day", "Harbour Fish", "Calamari Corner", "Blue Marlin", "Salt & Sea"]),
    (["Healthy", "Vegan"], ["Green Bowl", "Root & Leaf", "Plant Pantry", "Fresh Press", "Daily Greens"]),
    (["Bakery", "Breakfast"], ["Daily Bread", "Sunrise Cafe", "Crumb & Co", "Morning Glory", "Bean There"]),
    (["Desserts", "Ice Cream"], ["Sweet Tooth", "Scoop Shop", "Cake Lab", "Sugar Rush"]),
]

FEES_CENTS = [0, 0, 1200, 1500, 1800, 2200, 2900, 3500]

# Chance that a field is missing. Half of the misses omit the key, half send an explicit null.
GAP_CHANCE = {
    "rating": 0.08,
    "delivery_fee_cents": 0.04,
    "eta_minutes": 0.06,
    "image_url": 0.12,
    "is_open": 0.02,
    "cuisines": 0.02,
}


def image(restaurant_id):
    return f"https://picsum.photos/seed/{restaurant_id}/640/360"


def with_gaps(record, rng):
    for key, chance in GAP_CHANCE.items():
        roll = rng.random()
        if roll < chance / 2:
            record.pop(key, None)
        elif roll < chance:
            record[key] = None
    return record


def build():
    rng = random.Random(SEED)
    records = [
        {
            "id": "r-1001", "name": "Cape Town Grill", "cuisines": ["Steakhouse", "Grill"],
            "rating": 4.6, "delivery_fee_cents": 1500, "eta_minutes": 32,
            "is_open": True, "image_url": image("r-1001"),
        },
        {
            "id": "r-1002", "name": "Sushi on Sea Point", "cuisines": ["Sushi", "Japanese"],
            "rating": 4.3, "delivery_fee_cents": 0, "eta_minutes": 25,
            "is_open": False, "image_url": image("r-1002"),
        },
    ]
    used_names = {record["name"] for record in records}
    next_id = 1003

    while len(records) < TOTAL_VALID:
        cuisines, bases = rng.choice(GROUPS)
        base = rng.choice(bases)
        suburb = rng.choice(SUBURBS)
        name = f"{base} {suburb}" if base.endswith(" on") or rng.random() < 0.6 else base
        if name in used_names:
            continue
        used_names.add(name)

        restaurant_id = f"r-{next_id}"
        next_id += 1
        record = {
            "id": restaurant_id,
            "name": name,
            "cuisines": list(cuisines),
            "rating": round(rng.uniform(3.2, 4.9), 1),
            "delivery_fee_cents": rng.choice(FEES_CENTS),
            "eta_minutes": rng.randint(15, 60),
            "is_open": rng.random() < 0.7,
            "image_url": image(restaurant_id),
        }
        records.append(with_gaps(record, rng))

    # Records a real feed sometimes contains. The app must drop these, not crash.
    records.append({"id": "r-9001", "rating": 4.1})
    records.append({"name": "Diner Without Id", "is_open": True})
    return {"restaurants": records}


if __name__ == "__main__":
    data = build()
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    OUTPUT.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Wrote {len(data['restaurants'])} records to {OUTPUT}")
