#!/usr/bin/env bash
set -euo pipefail

: "${PGHOST:=localhost}"
: "${PGPORT:=5432}"
: "${PGUSER:=gini_ci}"
: "${PGPASSWORD:?PGPASSWORD must be set}"

database="gini_quadro_migration_test_$$_${RANDOM}"
export PGHOST PGPORT PGUSER PGPASSWORD

dropdb --if-exists "$database"
createdb "$database"
trap 'dropdb --if-exists "$database"' EXIT

for migration in src/main/resources/db/migration/V1_0_[0-5]__*.sql; do
  echo "Applying legacy schema: $migration"
  psql -X -v ON_ERROR_STOP=1 -d "$database" -f "$migration"
done

psql -X -v ON_ERROR_STOP=1 -d "$database" \
  -f src/test/resources/db/migration/legacy_quadro_horario_fixture.sql

# Uma alocação com turma de outro curso deve interromper toda a migração.
psql -X -v ON_ERROR_STOP=1 -d "$database" -c "
  INSERT INTO curso (id_curso, nome, periodicidade, duracao, status)
  VALUES (101, 'Curso divergente', 'Semestral', 6, 'ATIVO');
  UPDATE turma SET id_curso = 101 WHERE id_turma = 101;"
if psql -X -v ON_ERROR_STOP=1 -d "$database" \
  -c 'BEGIN' \
  -f src/main/resources/db/migration/V1_0_6__quadro_horario_por_turma.sql \
  -c 'COMMIT'; then
  echo 'A migração deveria rejeitar turma e quadro de cursos diferentes.' >&2
  exit 1
fi
psql -X -v ON_ERROR_STOP=1 -d "$database" -c "
  DO \$\$ BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = 'public' AND table_name = 'quadro_horario'
                 AND column_name = 'id_turma') THEN
      RAISE EXCEPTION 'Migração rejeitada deixou alterações parciais';
    END IF;
  END \$\$;
  UPDATE turma SET id_curso = 100 WHERE id_turma = 101;"

psql -X -v ON_ERROR_STOP=1 -d "$database" \
  -c 'BEGIN' \
  -f src/main/resources/db/migration/V1_0_6__quadro_horario_por_turma.sql \
  -c 'COMMIT'
psql -X -v ON_ERROR_STOP=1 -d "$database" \
  -f src/test/resources/db/migration/assert_quadro_horario_por_turma.sql

echo "Legacy timetable migration assertions passed."
