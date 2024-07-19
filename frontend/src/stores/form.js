import { ref } from "vue";

const useFormInput = (fields) => {
	const objects = {};
	for (let field of fields) {
		objects[field] = ref({ value: "", isFocused: false });
	}

	const handleFocus = (field) => {
		field.isFocused = true;
	};

	const handleBlur = (field) => {
		field.isFocused = false;
	};

	return {
		objects,
		handleFocus,
		handleBlur,
	};
};

export { useFormInput };
