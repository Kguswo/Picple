<script setup>
import ButtonComp from "@/components/account/ButtonComp.vue";

defineProps({
    label: String,
    textLabel: String,
    textButton: String,
    type: String,
    isDisabled: String,
    display: String,
})
</script>

<template>
    <div class="flex-col">
        <label :for="label" class="">{{ textLabel }}</label>
        <div :style="{ display }">
            <input :type="type" class="input-big" :class="{ 'background-color-disabled': isDisabled == 'true' }"
                :id="label" :disabled="isDisabled == 'true'" />
            <ButtonComp v-if="display == 'flex'" type="check">{{ textButton }}</ButtonComp>
        </div>
    </div>
</template>

<style scoped></style>