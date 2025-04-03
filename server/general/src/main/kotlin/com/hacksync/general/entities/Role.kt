package com.hacksync.general.entities

enum class Role {
    PARTICIPANT,
    CAPTAIN,
    MODERATOR;

    companion object {
        /**
         * Validates string of role and returns default role if validation fails
         * @param role string to validate
         * @param default default value if validation fails
         * @return validated Role enum value
         */
        fun validate(role: String, default: Role = PARTICIPANT): Role {
            return try {
                valueOf(role.uppercase())
            } catch (e: IllegalArgumentException) {
                default
            }
        }
    }
}

