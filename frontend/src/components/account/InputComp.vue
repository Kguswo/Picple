<script setup>
import ButtonComp from "@/components/account/ButtonComp.vue";

defineProps({
    label: String,
    textLabel: String,
    textButton: String,
    type: String,
    isDisabled: String,
    withButton: String,
})
</script>

<template>
    <div class="flex flex-col">
        <label :for="label" class="">{{ textLabel }}</label>
        <div :class="{ flex: withButton == 'true' }">
            <input :type="type" class="border-normal width-100 height-40px"
                :class="{ 'disabled-color': isDisabled == 'true' }" :id="label" :disabled="isDisabled == 'true'" />
            <ButtonComp v-if="withButton == 'true'" type="check">{{ textButton }}</ButtonComp>
        </div>
    </div>
</template>

<style scoped></style>