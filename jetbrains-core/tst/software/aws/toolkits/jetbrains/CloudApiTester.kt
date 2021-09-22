// Copyright 2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package software.aws.toolkits.jetbrains

import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Test
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudcontrol.CloudControlClient
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.aws.toolkits.core.utils.outputStream
import software.aws.toolkits.jetbrains.services.dynamic.DynamicResourcesProvider
import java.nio.file.Paths

class CloudApiTester {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    @Ignore
    fun whatBlowsUp() {
        val results = Paths.get("./cloudApiBlowUp.csv")
        results.outputStream().bufferedWriter().use {
            val cfnClient = CloudFormationClient.builder()
                .region(Region.US_WEST_2)
                .build()
            val client = CloudControlClient.builder()
                .region(Region.US_WEST_2)
                .build()
            val provider = DynamicResourcesProvider(cfnClient)
            val listSupportedTypes = runBlocking {
                provider.listSupportedTypes()
            }

            it.write("resource,status,details,time\n")

            listSupportedTypes.forEach { type ->
                it.write(
                    buildList {
                        println(type.fullName)
                        add(type.fullName)

                       /* while (true) {
                            try {
                                if (DynamicResources.listResources(type.fullName).isEmpty()) {
                                    add("Worked - empty")
                                } else {
                                    add("Worked - resources")
                                }
                                add("")
                                break
                            } catch (e: AwsServiceException) {
                                if (e.isThrottlingException) {
                                    Thread.sleep(3000)
                                } else {
                                    if (e.message?.contains("does not support LIST action") == true) {
                                        add("Failed - Unsupported")
                                    } else {
                                        add("Failed - Other")
                                    }

                                    add("\"${e.message}\"")

                                    break
                                }
                            } finally {
                                add(DateTimeFormatter.ISO_DATE_TIME.format(Instant.now().atOffset(ZoneOffset.UTC)))
                            }
                        }*/
                    }.joinToString(separator = ",", postfix = "\n")
                )
            }
        }

        println("Results written to ${results.toAbsolutePath()}")
    }
}
