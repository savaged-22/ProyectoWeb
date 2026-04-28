import os
import re

def refactor_java_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                path = os.path.join(root, file)
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # Add import if not present and UUID is used
                needs_import = False
                
                # Refactor Entity IDs
                # private Integer id; -> private UUID id;
                if '@Id' in content and 'private Integer id;' in content:
                    content = re.sub(r'private Integer id;', r'private UUID id;', content)
                    content = re.sub(r'@GeneratedValue\(strategy\s*=\s*GenerationType\.IDENTITY\)', r'@GeneratedValue(strategy = GenerationType.UUID)', content)
                    needs_import = True
                    
                # Refactor generic parameters and variables
                replacements = [
                    (r'private Integer id;', r'private UUID id;'),
                    (r'JpaRepository<Empresa, Integer>', r'JpaRepository<Empresa, UUID>'),
                    (r'JpaRepository<Usuario, Integer>', r'JpaRepository<Usuario, UUID>'),
                    (r'JpaRepository<Pool, Integer>', r'JpaRepository<Pool, UUID>'),
                    (r'JpaRepository<RolPool, Integer>', r'JpaRepository<RolPool, UUID>'),
                    (r'JpaRepository<RolProceso, Integer>', r'JpaRepository<RolProceso, UUID>'),
                    (r'JpaRepository<Proceso, Integer>', r'JpaRepository<Proceso, UUID>'),
                    (r'JpaRepository<ProcesoCompartido, Integer>', r'JpaRepository<ProcesoCompartido, UUID>'),
                    (r'JpaRepository<MensajeProceso, Integer>', r'JpaRepository<MensajeProceso, UUID>'),
                    (r'JpaRepository<SuscripcionMensaje, Integer>', r'JpaRepository<SuscripcionMensaje, UUID>'),
                    (r'JpaRepository<EntregaMensaje, Integer>', r'JpaRepository<EntregaMensaje, UUID>'),
                    (r'JpaRepository<NotificacionExterna, Integer>', r'JpaRepository<NotificacionExterna, UUID>'),
                    (r'List<Integer>', r'List<UUID>')
                ]

                original_content_for_check = content
                for old, new in replacements:
                    content = content.replace(old, new)
                    
                # Replace Integer somethingId with UUID somethingId
                content = re.sub(r'Integer\s+([a-zA-Z0-9_]*Id\b)', r'UUID \1', content)
                content = re.sub(r'Integer\s+(id\b)', r'UUID \1', content) # for getById(Integer id)
                    
                if content != original_content_for_check:
                    needs_import = True

                if needs_import and 'import java.util.UUID;' not in content:
                    # insert after package
                    content = re.sub(r'(package com\.lulo\.[^;]+;)', r'\1\n\nimport java.util.UUID;', content)

                with open(path, 'w', encoding='utf-8') as f:
                    f.write(content)

if __name__ == '__main__':
    refactor_java_files('src/main/java')
    refactor_java_files('src/test/java')
