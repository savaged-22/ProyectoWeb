import os
import re

def refactor_sql_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.sql'):
                path = os.path.join(root, file)
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # Change SERIAL PRIMARY KEY to UUID PRIMARY KEY DEFAULT gen_random_uuid()
                content = re.sub(r'id\s+SERIAL\s+PRIMARY\s+KEY', r'id UUID PRIMARY KEY DEFAULT gen_random_uuid()', content, flags=re.IGNORECASE)
                
                # Change INTEGER for foreign keys to UUID
                content = re.sub(r'empresa_id\s+INTEGER', r'empresa_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'usuario_id\s+INTEGER', r'usuario_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'proceso_id\s+INTEGER', r'proceso_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'pool_id\s+INTEGER', r'pool_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'rol_id\s+INTEGER', r'rol_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'created_by_user_id\s+INTEGER', r'created_by_user_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'proceso_origen_id\s+INTEGER', r'proceso_origen_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'proceso_destino_id\s+INTEGER', r'proceso_destino_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'mensaje_id\s+INTEGER', r'mensaje_id UUID', content, flags=re.IGNORECASE)
                content = re.sub(r'suscripcion_id\s+INTEGER', r'suscripcion_id UUID', content, flags=re.IGNORECASE)

                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)

if __name__ == '__main__':
    refactor_sql_files('src/main/resources/db/migration')
