declare module 'react-native-brightness' {
    export function hasPermission(): Promoise<boolean>;
    export function requestPermission(): Promoise<boolean>;
    export function getBrightness(): Promise<number>;
    export function setBrightness(brightnessLevel: number): Promise<boolean>;
    export function getBrightnessMode(): Promise<string>;
    export function setBrightness(mode: string): Promise<boolean>;
}