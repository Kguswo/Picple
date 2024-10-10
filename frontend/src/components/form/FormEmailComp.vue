<script setup>
import FormInputComp from '@/components/form/FormInputComp.vue';
import FormButtonComp from '@/components/form/FormButtonComp.vue';
import { validateEmailPattern, setFormMessage } from '@/assets/js/validation';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { sendAuthNumberApi, sendAuthNumberByFindApi, verifyAuthNumberApi } from '@/api/userApi';
import { useUserStore } from '@/stores/userStore';
import { storeToRefs } from 'pinia';
import { useFormStore } from '@/stores/formStore';
import { alertResult } from '@/api/baseApi';
import { throttle } from '@/assets/js/util';

const props = defineProps({
    name: String,
});

const router = useRouter();
const userStore = useUserStore();
const formStore = useFormStore();

const { verifiedEmail } = storeToRefs(userStore);
const { email, authNumber, emailField, authNumberField } = storeToRefs(formStore);
formStore.initForm([email, authNumber], [emailField, authNumberField]);

const isSend = ref(false);
const lastCall = ref(0);

const validateInputField = () => {
    emailField.value.message = validateEmailPattern(email.value.value);
    if (formStore.focusInputField(emailField)) {
        return false;
    }
    return true;
};

const onClickSend = (e) => {
    e.stopPropagation();
    if (!validateInputField()) {
        return;
    }
    throttle(lastCall, sendAuthNumber, 10000)();
};

const sendAuthNumber = async () => {
    const { data } =
        props.name === 'signup'
            ? await sendAuthNumberApi(email.value.value)
            : await sendAuthNumberByFindApi(email.value.value);
    if (data.code == import.meta.env.VITE_CODE_DUPLICATED_USER_EMAIL) {
        await alertResult(false, '해당 이메일이 이미 존재합니다.');
        router.go(0);
        return;
    }
    if (!data.isSuccess) {
        await alertResult(false, '오류가 발생하였습니다. 다시 시도해주십시오.');
        router.go(0);
        return;
    }
    isSend.value = true;
    await alertResult(true, '인증번호가 전송되었습니다.');
};

const verifyEmail = async () => {
    emailField.value.message = !isSend.value
        ? setFormMessage(`이메일 인증이 필요합니다.`, true)
        : setFormMessage(``, false);
    authNumberField.value.message = !authNumber.value.value
        ? setFormMessage(`인증번호를 입력해주세요.`, true)
        : setFormMessage(``, false);
    if (emailField.value.message.text) {
        emailField.value.focusInput();
        return;
    }
    if (authNumberField.value.message.text) {
        authNumberField.value.focusInput();
        return;
    }

    const { data } = await verifyAuthNumberApi(email.value.value, authNumber.value.value);
    if (data.code == import.meta.env.VITE_CODE_NOT_EQUAL_EMAIL_CODE) {
        await alertResult(false, '인증번호가 일치하지 않습니다.');
        return;
    }
    if (!data.isSuccess) {
        await alertResult(false, '이메일 인증에 실패하였습니다.');
        return;
    }
    verifiedEmail.value = email.value.value;
    await alertResult(true, '이메일 인증에 성공했습니다.');
    router.push({ name: props.name });
};
</script>

<template>
    <form
        class="form-content"
        @keyup.enter.prevent="verifyEmail"
    >
        <FormInputComp
            :inputParams="email"
            :isSend="isSend"
            ref="emailField"
        >
            <FormButtonComp
                size="small"
                @click="onClickSend"
                :disabled="isSend"
                >전송</FormButtonComp
            >
        </FormInputComp>

        <FormInputComp
            :inputParams="authNumber"
            ref="authNumberField"
            class="mt-10"
        >
        </FormInputComp>

        <FormButtonComp
            size="big"
            @click="verifyEmail"
            >다음</FormButtonComp
        >
    </form>
</template>

<style scoped>
.form-button-small {
    background-color: #a7c7e7; /* 파스텔 블루 */
    color: white;
    font-size: 14px;
    border: none;
    border-radius: 10px;
    padding: 10px 15px;
    transition: background-color 0.3s ease, transform 0.2s ease;
}

.form-button-small:disabled {
    background-color: #d3e2f1; /* 비활성화 상태의 파스텔 블루 */
}

.form-button-small:hover:enabled {
    background-color: #7fb1e3; /* 호버 시 진한 파스텔 블루 */
}
</style>
