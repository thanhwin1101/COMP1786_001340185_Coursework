// App.js
import React, { useEffect } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import HikeListScreen from './src/screens/HikeListScreen';
import HikeFormScreen from './src/screens/HikeFormScreen';
import ObservationScreen from './src/screens/ObservationScreen';
import { initDatabase } from './src/storage/db';

const Stack = createNativeStackNavigator();

export default function App() {
  useEffect(() => {
    initDatabase().catch(err => console.log('DB init error', err));
  }, []);

  return (
    <NavigationContainer>
      <Stack.Navigator
        screenOptions={{
          headerShown: false,       // ❗Ẩn header mặc định của Stack
        }}
      >
        <Stack.Screen
          name="HikeList"
          component={HikeListScreen}
        />
        <Stack.Screen
          name="HikeForm"
          component={HikeFormScreen}
        />
        <Stack.Screen
          name="Observations"
          component={ObservationScreen}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
}
