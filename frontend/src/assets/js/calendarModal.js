import { ref, watch } from "vue";
import photo1 from "@/assets/img/calendar/portrait_48.png";
import photo2 from "@/assets/img/calendar/landscape_84.png";
import photo3 from "@/assets/img/calendar/portrait_messi3.png";
import photo4 from "@/assets/img/calendar/portrait_jj4.jpeg";
import photo5 from "@/assets/img/calendar/landscape_lion.png";

// Example data structure for photos with associated dates
const allPhotos = [
    { src: photo1, date: "2024-07-17" },
    { src: photo2, date: "2024-07-18" },
    { src: photo3, date: "2024-07-17" },
    { src: photo4, date: "2024-07-18" },
    { src: photo5, date: "2024-07-19" },
];

export default function useCalendarModal(props, emit) {
    const photos = ref([]);
    const currentIndex = ref(0);
    const currentPhotoExpanded = ref(false);

    const close = () => {
        emit("close");
    };

    const prevPhoto = () => {
        currentIndex.value =
            (currentIndex.value - 1 + photos.value.length) %
            photos.value.length;
    };

    const nextPhoto = () => {
        currentIndex.value = (currentIndex.value + 1) % photos.value.length;
    };

    const getPrevPhoto = () => {
        const prevIndex =
            (currentIndex.value - 1 + photos.value.length) %
            photos.value.length;
        return photos.value[prevIndex]?.src;
    };

    const getCurrentPhoto = () => {
        return photos.value[currentIndex.value]?.src;
    };

    const getNextPhoto = () => {
        const nextIndex = (currentIndex.value + 1) % photos.value.length;
        return photos.value[nextIndex]?.src;
    };

    const toggleCurrentPhoto = () => {
        currentPhotoExpanded.value = !currentPhotoExpanded.value;
    };

    const getPhotoClass = (photo) => {
        if (!photo) return "";
        if (photo.includes("portrait")) {
            return "portrait";
        } else if (photo.includes("landscape")) {
            return "landscape";
        }
        return "";
    };

    // Watch for changes in selectedDate and update photos accordingly
    watch(
        () => props.selectedDate,
        (newDate) => {
            const filteredPhotos = allPhotos.filter(
                (photo) => photo.date === newDate
            );
            if (filteredPhotos.length > 0) {
                photos.value = filteredPhotos;
                currentIndex.value = 0;
            } else {
                photos.value = [];
            }
        },
        { immediate: true }
    );

    return {
        photos,
        currentIndex,
        currentPhotoExpanded,
        close,
        prevPhoto,
        nextPhoto,
        getPrevPhoto,
        getCurrentPhoto,
        getNextPhoto,
        toggleCurrentPhoto,
        getPhotoClass,
    };
}
