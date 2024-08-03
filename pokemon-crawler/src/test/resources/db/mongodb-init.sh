mongoimport --jsonArray --db test --collection battle --file /docker-entrypoint-initdb.d/battle.json
mongoimport --jsonArray --db test --collection battle_team --file /docker-entrypoint-initdb.d/battle_team.json
mongoimport --jsonArray --db test --collection battle_stat --file /docker-entrypoint-initdb.d/battle_stat.json
mongoimport --jsonArray --db test --collection ladder --file /docker-entrypoint-initdb.d/ladder.json
mongoimport --jsonArray --db test --collection monthly_stat_meta --file /docker-entrypoint-initdb.d/metaStat.json
mongoimport --jsonArray --db test --collection monthly_stat_pokemon_moveset --file /docker-entrypoint-initdb.d/moveSet.json
mongoimport --jsonArray --db test --collection monthly_stat_pokemon_usage --file /docker-entrypoint-initdb.d/usage.json
mongoimport --jsonArray --db test --collection pokemon_set --file /docker-entrypoint-initdb.d/pokemonSet.json