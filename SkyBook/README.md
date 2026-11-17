# SkyBook

## Project Information

Project ID: CP-046

Project Name: SkyBook

Full Title: Dynamic-Fare Flight Reservation and Waitlist Engine

Domain: Travel, Transport and Logistics

Language: Java

Difficulty: Intermediate

## Project Overview

SkyBook is a Java-based console application that
simulates a flight reservation system.

It implements dynamic airfare calculation,
flight booking, FIFO waitlist management,
booking cancellation, automatic passenger promotion,
PNR-based lookup and flight load-factor reporting.

## Features

1. Flight Management
2. Flight Search
3. Dynamic Fare Calculation
4. Passenger Booking
5. FIFO Waitlist Management
6. Booking Cancellation
7. Automatic Waitlist Promotion
8. PNR-Based Booking Lookup
9. Economy to Business Upgrade
10. Flight Load Factor Report

## Technologies

- Java 17
- Object-Oriented Programming
- ArrayList
- HashMap
- Queue
- Exception Handling
- Java Collections Framework

## Requirements

- JDK 17 or newer
- Windows operating system
- VS Code or any Java IDE

## Execution

Run the following commands from the project directory:

javac -d out src/*.java

java -cp out Main

Alternatively, double-click run.bat.

## Automated Testing

Compile:

javac -d out src/*.java test/*.java

Execute:

java -ea -cp out SkyBookTest

## Storage

This version uses in-memory collections.
Booking records are not permanently stored after
the application terminates.

## Project Type

Menu-driven Java console application.