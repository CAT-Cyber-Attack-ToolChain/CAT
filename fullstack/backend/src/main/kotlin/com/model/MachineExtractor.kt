package com.model

class MachineExtractor {

    companion object {
        fun extract(permission : String) : List<String> {
            val machines = mutableListOf<String>()
            val props = permission.replace(Regex("\\s"), "").replace('(', ',').dropLast(1).split(",")
            when (props[0]) {
                "netAccess" -> {
                    machines.add(props[1])
                    machines.add(props[2])
                }

                "execCode" -> {
                    machines.add(props[1])
                    machines.add(props[2])
                }

                "hacl" -> {
                    machines.add(props[1])
                    machines.add(props[2])
                }

                "vulExists" -> {
                    machines.add(props[1])
                }

                "setuidProgramInfo" -> {
                    machines.add(props[1])
                }

                "inSubnet" -> {
                    machines.add(props[1])
                }

                "networkService" -> {
                    machines.add(props[1])
                }

                "hasAccount" -> {
                    machines.add(props[1])
                }

                "setuidProgram" -> {
                    machines.add(props[1])
                }

                "networkServiceInfo" -> {
                    machines.add(props[1])
                }
            }

            return machines
        }
    }

}