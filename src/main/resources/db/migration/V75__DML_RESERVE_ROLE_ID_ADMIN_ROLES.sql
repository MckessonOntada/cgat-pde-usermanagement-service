-- Reserve IDs 1–20 only if sequence is still below 21
DO $$
BEGIN
  IF (SELECT last_value FROM usermanagement.role_sequence) < 21 THEN
    PERFORM setval('usermanagement.role_sequence', 20, false); -- nextval() will return 21
  END IF;
END
$$;

-- Shift role IDs >= 4 and update mapping table accordingly
DO $$
DECLARE
  current_seq bigint;
BEGIN
  -- Step 1: Get current value of role_sequence
  SELECT last_value INTO current_seq FROM usermanagement.role_sequence;

  -- Step 2: Create temporary mapping table (old_id → new_id)
  CREATE TEMP TABLE role_id_map ON COMMIT DROP AS
  SELECT id AS old_id,
         ROW_NUMBER() OVER (ORDER BY id) + current_seq AS new_id
  FROM usermanagement.role
  WHERE id >= 4;

  -- Step 3: Update role_permissions_mapping with new role_ids
  UPDATE usermanagement.role_permissions_mapping rpm
  SET role_id = rim.new_id
  FROM role_id_map rim
  WHERE rpm.role_id = rim.old_id;

  -- Step 4: Update user table with new role_ids
  UPDATE usermanagement."user" u
  SET role_id = rim.new_id
  FROM role_id_map rim
  WHERE u.role_id = rim.old_id;

  -- Step 5: Update the role table with new IDs
  UPDATE usermanagement.role r
  SET id = rim.new_id
  FROM role_id_map rim
  WHERE r.id = rim.old_id;

  -- Step 5: Sync the sequence to the new max ID in role
  PERFORM setval('usermanagement.role_sequence', (SELECT MAX(id) FROM usermanagement.role), true);
END
$$;